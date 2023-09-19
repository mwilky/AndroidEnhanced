package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setObjectField

class Lockscreen {

    companion object {
        //Hook Classes
        private const val KEYGUARD_STATUSBAR_VIEW_CLASS =
            "com.android.systemui.statusbar.phone.KeyguardStatusBarView"
        private const val KEYGUARD_QUICK_AFFORDANCE_VIEW_MODEL_CLASS =
            "com.android.systemui.keyguard.ui.viewmodel.KeyguardQuickAffordanceViewModel"
        private const val KEYGUARD_BOTTOM_AREA_VIEW_BINDER_CLASS =
            "com.android.systemui.keyguard.ui.binder.KeyguardBottomAreaViewBinder"
        private const val KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS =
            "com.android.keyguard.KeyguardAbsKeyInputView"
        private const val NUM_PAD_KEY_CLASS =
            "com.android.keyguard.NumPadKey"
        private const val QS_FOOTER_VIEW_CONTROLLER =
            "com.android.systemui.qs.QSFooterViewController"
        private const val PHONE_WINDOW_MANAGER_9_CLASS =
            "com.android.server.policy.PhoneWindowManager\$9"
        private const val PHONE_WINDOW_MANAGER_CLASS =
            "com.android.server.policy.PhoneWindowManager"
        private const val FOOTER_ACTIONS_VIEW_MODEL_CLASS =
            "com.android.systemui.qs.footer.ui.viewmodel.FooterActionsViewModel"
        private const val FOOTER_ACTIONS_VIEW_BINDER_CLASS =
            "com.android.systemui.qs.footer.ui.binder.FooterActionsViewBinder"

        //Class Objects
        lateinit var keyguardStatusBarView: Any
        var powerMenuButton: Any? = null

        //Tweak Variables
        var hideLockscreenStatusbarEnabled: Boolean = false
        var hideLockscreenShortcutsEnabled: Boolean = false
        var scrambleKeypadEnabled: Boolean = false
        var mDisableLockscreenPowerMenuEnabled = false

        //Scramble Keypad
        private var sNumbers: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

        fun initFramework(classLoader: ClassLoader?) {

            //Disable power menu lockscreen
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_9_CLASS,
                classLoader,
                "execute",
                executeHook
            )

            //Disable power menu lockscreen
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "powerLongPress",
                Long::class.javaPrimitiveType,
                powerLongPressHook
            )

        }

        fun initSystemUI(classLoader: ClassLoader?) {

            //Hide lockscreen statusbar
            findAndHookMethod(
                KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "updateVisibilities",
                updateVisibilitiesHook
            )

            //Hide lockscreen statusbar
            findAndHookMethod(
                KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                onFinishInflateHook
            )

            //Hide lockscreen shortcuts
            val keyguardQuickAffordanceViewModelClass =
                findClass(
                    KEYGUARD_QUICK_AFFORDANCE_VIEW_MODEL_CLASS,
                    classLoader
                )

            //Hide lockscreen shortcuts
            hookAllConstructors(
                keyguardQuickAffordanceViewModelClass,
                keyguardQuickAffordanceViewModelConstructorHook
            )

            /*val falsingManager =
                findClass(
                    "com.android.systemui.plugins.FalsingManager",
                    classLoader
                )

            val function1 =
                findClass(
                    "kotlin.jvm.functions.Function1",
                    classLoader
                )

            val vibrationHelper =
                findClass(
                    "com.android.systemui.statusbar.VibratorHelper",
                    classLoader
                )

            //TODO: we may be able to go into further control from this function. Check it at a later date
            findAndHookMethod(
                KEYGUARD_BOTTOM_AREA_VIEW_BINDER_CLASS,
                classLoader,
                "access\$updateButton",
                ImageView::class.java,
                keyguardQuickAffordanceViewModelClass,
                falsingManager,
                function1,
                vibrationHelper,
                updateButtonHook
            )*/

            //Scramble Keypad
            findAndHookMethod(
                KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                onViewAttachedHook
            )

            //Scramble Keypad
            val numPadKeyClass = findClass(
                NUM_PAD_KEY_CLASS,
                classLoader
            )

            //Scramble Keypad
            hookAllConstructors(
                numPadKeyClass,
                NumPadKeyConstructorHook
            )

            //Disable power menu lockscreen
            findAndHookMethod(
                QS_FOOTER_VIEW_CONTROLLER,
                classLoader,
                "setKeyguardShowing",
                Boolean::class.javaPrimitiveType,
                setKeyguardShowingHook
            )

            val footerActionsViewModel =
                findClass(
                    FOOTER_ACTIONS_VIEW_MODEL_CLASS,
                    classLoader
                )
            hookAllConstructors(
                footerActionsViewModel,
                FooterActionsViewModelConstructorHook
            )

            val iconButtonViewHolder =
                findClass(
                    "com.android.systemui.qs.footer.ui.binder.IconButtonViewHolder",
                    classLoader
                )
            val footerActionsButtonViewModel =
                findClass(
                    "com.android.systemui.qs.footer.ui.viewmodel.FooterActionsButtonViewModel",
                    classLoader
                )
            findAndHookMethod(
                FOOTER_ACTIONS_VIEW_BINDER_CLASS,
                classLoader,
                "bindButton",
                iconButtonViewHolder,
                footerActionsButtonViewModel,
                bindButtonHook
            )

        }

        //Hooked functions
        private val onFinishInflateHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                keyguardStatusBarView = param.thisObject

                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                // Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.hideLockscreenStatusBar,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.hideLockscreenShortcuts,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.scrambleKeypad,
                    param.thisObject.toString()
                )

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.disableLockscreenPowerMenu,
                    param.thisObject.toString()
                )

            }
        }

        //Hide Lockscreen Shortcuts
        private val updateVisibilitiesHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                if (hideLockscreenStatusbarEnabled)
                    callMethod(param.thisObject, "setVisibility", View.GONE)
            }
        }

        //Hide Lockscreen Shortcuts
        private val keyguardQuickAffordanceViewModelConstructorHook:
                XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                if (hideLockscreenShortcutsEnabled)
                    setBooleanField(param.thisObject, "isVisible", false)
            }
        }

        //Scramble Keypad
        private val onViewAttachedHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (scrambleKeypadEnabled)
                    sNumbers.shuffle()
            }
        }

        //Scramble Keypad
        private val NumPadKeyConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (scrambleKeypadEnabled) {
                    val mDigitText = getObjectField(param.thisObject, "mDigitText")
                    val mDigit = getIntField(param.thisObject, "mDigit")
                    setObjectField(param.thisObject, "mDigit", sNumbers[mDigit])
                    callMethod(mDigitText, "setText", sNumbers[mDigit].toString())
                }
            }
        }

        //Block power menu
        //Toggle the visibility of the power menu button when we go/leave lockscreen
        private val setKeyguardShowingHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val keyguard = param.args[0] as Boolean

                //If on lockscreen and tweak is enabled, hide the button
                if (powerMenuButton != null) {
                    if (!keyguard || !mDisableLockscreenPowerMenuEnabled) {
                        (powerMenuButton as View).visibility = View.VISIBLE
                    } else {
                        (powerMenuButton as View).visibility = View.GONE
                    }
                }
            }
        }

        //Block power menu
        // Blocks power menu on a regular power button press
        private val powerLongPressHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val isScreenOn = callMethod(param.thisObject, "isScreenOn")
                        as Boolean
                val keyguardOn = callMethod(param.thisObject, "keyguardOn")
                        as Boolean
                val resolvedLongPressOnPowerBehavior =
                    callMethod(param.thisObject, "getResolvedLongPressOnPowerBehavior")
                            as Int
                if (resolvedLongPressOnPowerBehavior == 1) {
                    if (mDisableLockscreenPowerMenuEnabled && isScreenOn && keyguardOn) {
                        setBooleanField(param.thisObject, "mPowerKeyHandled", true)
                        callMethod(
                            param.thisObject,
                            "performHapticFeedback",
                            10003,
                            false,
                            "Power - Long Press - Global Actions Suppressed"
                        )
                        param.result = null
                    }
                }
            }
        }

        //Block power menu
        // Blocks power menu on a regular power button press
        private val executeHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val isScreenOn =
                    callMethod(
                        XposedHelpers.getSurroundingThis(param.thisObject),
                        "isScreenOn"
                    ) as Boolean

                val keyguardOn =
                    callMethod(
                        XposedHelpers.getSurroundingThis(param.thisObject),
                        "keyguardOn"
                    ) as Boolean

                val mPowerVolUpBehavior =
                    getIntField(
                        XposedHelpers.getSurroundingThis(param.thisObject),
                        "mPowerVolUpBehavior"
                    )
                if (mPowerVolUpBehavior == 2) {
                    if (mDisableLockscreenPowerMenuEnabled && isScreenOn && keyguardOn) {
                        callMethod(
                            XposedHelpers.getSurroundingThis(param.thisObject),
                            "performHapticFeedback",
                            10003,
                            false,
                            "Power + Volume Up - Global Actions Suppressed"
                        )
                        param.result = null
                    }
                }
            }
        }

        private val FooterActionsViewModelConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                log("$TAG: in here")
            }
        }

        private val bindButtonHook: XC_MethodHook = object : XC_MethodHook() {
            @SuppressLint("DiscouragedApi")
            override fun afterHookedMethod(param: MethodHookParam) {

                val view = getObjectField(param.args[0], "view")
                    as View
                val context = view.context

                //Get the power menu button ID
                val pmLite = context.resources.getIdentifier(
                    "pm_lite",
                    "id",
                    "com.android.systemui"
                )
                //Set powerMenuButton so we can toggle the visibility in setKeyguardShowingHook
                if (view.id == pmLite)
                    powerMenuButton = view

            }
        }

        //Additional functions
    }
}