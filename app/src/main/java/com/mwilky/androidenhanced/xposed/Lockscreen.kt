package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
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
        private const val QS_FRAGMENT_CLASS =
            "com.android.systemui.qs.QSFragment"
        private const val CENTRAL_SURFACES_COMMAND_QUEUE_CALLBACKS_CLASS =
            "com.android.systemui.statusbar.phone.CentralSurfacesCommandQueueCallbacks"

        //Class Objects
        lateinit var keyguardStatusBarView: Any
        var powerMenuButton: Any? = null

        //Tweak Variables
        var hideLockscreenStatusbarEnabled: Boolean = false
        var hideLockscreenShortcutsEnabled: Boolean = false
        var scrambleKeypadEnabled: Boolean = false
        var mDisableLockscreenPowerMenuEnabled = false
        var mDisableLockscreenQuicksettingsEnabled = false

        //Scramble Keypad
        private var sNumbers: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

        //Disable QS on lockscreen
        private const val DISABLE2_QUICK_SETTINGS: Int = 0x00000001
        private const val DISABLE2_NOTIFICATION_SHADE: Int = 1 shl 2
        private const val DISABLE_EXPAND: Int = 0x00010000
        private const val DISABLE_NOTIFICATION_ALERTS: Int = 0x00040000

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

            findAndHookMethod(
                QS_FRAGMENT_CLASS,
                classLoader,
                "disable",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                disableHookQSFragment
            )

            findAndHookMethod(
                CENTRAL_SURFACES_COMMAND_QUEUE_CALLBACKS_CLASS,
                classLoader,
                "disable",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                disableHookCentralSurfaces
            )

        }

        //Hooked functions
        private val onFinishInflateHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                keyguardStatusBarView = param.thisObject

                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                //TODO: move all these to onCreate in SystemUIApplication
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

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.disableQsLockscreen,
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

        //Hide lockscreen shortcuts
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

        //disable qs on lockscreen
        private val disableHookQSFragment: XC_MethodHook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                val mContext = callMethod(param.thisObject, "getContext")
                        as Context
                val displayId = param.args[0] as Int
                var state2 = param.args[2] as Int

                if (displayId != mContext.display!!.displayId)
                    return null

                val mContainer =
                    getObjectField(param.thisObject, "mContainer")
                val mHeader =
                    getObjectField(param.thisObject, "mHeader")
                val mFooter =
                    getObjectField(param.thisObject, "mFooter")
                val mQuickQSPanel =
                    getObjectField(mHeader, "mHeaderQsPanel")
                            as View

                val mRemoteInputQuickSettingsDisabler =
                    getObjectField(param.thisObject, "mRemoteInputQuickSettingsDisabler")
                val mQSCustomizerController =
                    getObjectField(param.thisObject, "mQSCustomizerController")
                val mKeyguardStateController =
                    getObjectField(mQSCustomizerController, "mKeyguardStateController")

                state2 = adjustDisableFlags(mRemoteInputQuickSettingsDisabler, state2)
                state2 = adjustQsDisableFlags(mKeyguardStateController, state2)

                val disabled = (state2 and DISABLE2_QUICK_SETTINGS) != 0
                val mQsDisabled = getBooleanField(param.thisObject, "mQsDisabled")
                if (disabled == mQsDisabled) return null

                setBooleanField(param.thisObject, "mQsDisabled", disabled)
                setBooleanField(mContainer, "mQsDisabled", disabled)
                setBooleanField(mHeader, "mQsDisabled", disabled)
                setBooleanField(mQuickQSPanel, "mDisabledByPolicy", disabled)
                if (disabled) {
                    mQuickQSPanel.visibility = View.GONE
                } else {
                    mQuickQSPanel.visibility = View.VISIBLE
                }
                callMethod(mHeader, "updateResources")

                callMethod(mFooter, "disable", state2)
                callMethod(param.thisObject, "updateQsState")

                return null

            }
        }

        //disable qs on lockscreen
        private val disableHookCentralSurfaces: XC_MethodHook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                val displayId = param.args[0] as Int
                val state1 = param.args[1] as Int
                var state2 = param.args[2] as Int

                val mDisplayId = getIntField(param.thisObject, "mDisplayId")

                if (displayId != mDisplayId)
                    return null

                val mRemoteInputQuickSettingsDisabler =
                    getObjectField(param.thisObject, "mRemoteInputQuickSettingsDisabler")
                val mCentralSurfaces =
                    getObjectField(param.thisObject, "mCentralSurfaces")
                val mShadeController =
                    getObjectField(param.thisObject, "mShadeController")
                val mHeadsUpManager =
                    getObjectField(param.thisObject, "mHeadsUpManager")
                val mNotificationPanelViewController =
                    getObjectField(param.thisObject, "mNotificationPanelViewController")
                val mShadeHeaderController =
                    getObjectField(mNotificationPanelViewController, "mShadeHeaderController")
                val mKeyguardStateController =
                    getObjectField(param.thisObject, "mKeyguardStateController")

                state2 = adjustDisableFlags(mRemoteInputQuickSettingsDisabler, state2)
                state2 = adjustQsDisableFlags(mKeyguardStateController, state2)

                val old1: Int = callMethod(mCentralSurfaces, "getDisabled1")
                        as Int
                val diff1: Int = state1 xor old1
                callMethod(mCentralSurfaces, "setDisabled1", state1)

                val old2: Int = callMethod(mCentralSurfaces, "getDisabled2")
                        as Int
                val diff2: Int = state2 xor old2
                callMethod(mCentralSurfaces, "setDisabled2", state2)

                if (diff1 and DISABLE_EXPAND != 0) {
                    if (state1 and DISABLE_EXPAND != 0) {
                        callMethod(mShadeController, "animateCollapseShade")
                    }
                }

                val areNotificationAlertsDisabled =
                    callMethod(mCentralSurfaces, "areNotificationAlertsDisabled")
                            as Boolean
                if (diff1 and DISABLE_NOTIFICATION_ALERTS != 0) {
                    if (areNotificationAlertsDisabled) {
                        callMethod(mHeadsUpManager, "releaseAllImmediately")
                    }
                }

                if (diff2 and DISABLE2_QUICK_SETTINGS != 0) {
                    callMethod(mCentralSurfaces, "updateQsExpansionEnabled")
                }

                if (diff2 and DISABLE2_NOTIFICATION_SHADE != 0) {
                    callMethod(mCentralSurfaces, "updateQsExpansionEnabled")
                    if (state2 and DISABLE2_NOTIFICATION_SHADE != 0) {
                        callMethod(mShadeController, "animateCollapseShade")
                    }
                }

                val disabled = state2 and DISABLE2_QUICK_SETTINGS != 0


                val mShadeHeaderControllerQsDisabled =
                    getBooleanField(mShadeHeaderController, "qsDisabled")

                if (disabled != mShadeHeaderControllerQsDisabled) {
                    setBooleanField(mShadeHeaderController, "qsDisabled", disabled)
                    callMethod(mShadeHeaderController, "updateVisibility")
                }

                return null

            }
        }

        //Additional functions
        //disable qs on lockscreen
        //Moved this functionality out of the disable functions so we didn't have to recreate it twice
        fun adjustDisableFlags(mRemoteInputQuickSettingsDisabler: Any, state: Int): Int {
            var mutableState = state
            val remoteInputActive =
                getBooleanField(mRemoteInputQuickSettingsDisabler, "remoteInputActive")
            val isLandscape =
                getBooleanField(mRemoteInputQuickSettingsDisabler, "isLandscape")
            val shouldUseSplitNotificationShade = getBooleanField(
                mRemoteInputQuickSettingsDisabler,
                "shouldUseSplitNotificationShade"
            )
            if (remoteInputActive &&
                isLandscape &&
                !shouldUseSplitNotificationShade
            ) {
                mutableState = state or DISABLE2_QUICK_SETTINGS
            }
            return mutableState
        }

        //Sets flags based on if tweak is enabled and is on lockscreen
        private fun adjustQsDisableFlags(mKeyguardStateController: Any, state2: Int): Int {
            return if (mDisableLockscreenQuicksettingsEnabled && !isUnlocked(mKeyguardStateController)) {
                DISABLE2_QUICK_SETTINGS
            } else {
                state2
            }
        }

        //disable qs on lockscreen
        //Checks whether device is on lockscreen
        private fun isUnlocked(mKeyguardStateController: Any): Boolean {
            val isShowing =
                callMethod(mKeyguardStateController, "isShowing")
                    as Boolean
            val canDismissLockScreen =
                callMethod(mKeyguardStateController, "canDismissLockScreen")
                    as Boolean

            return !(isShowing && !canDismissLockScreen)
        }
    }
}