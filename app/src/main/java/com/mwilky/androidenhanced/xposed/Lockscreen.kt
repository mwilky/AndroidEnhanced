package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setObjectField
import java.util.Collections

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
        private const val NUM_PAD_KEY_CLASS = "com.android.keyguard.NumPadKey"

        //Class Objects
        lateinit var keyguardStatusBarView: Any

        //Tweak Variables
        var hideLockscreenStatusbarEnabled: Boolean = false
        var hideLockscreenShortcutsEnabled: Boolean = false
        var scrambleKeypadEnabled: Boolean = false

        //Scramble Keypad
        private var sNumbers: MutableList<Int> = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

        fun init(classLoader: ClassLoader?) {

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

        //Additional functions
    }
}