package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import com.mwilky.androidenhanced.HookedClasses.Companion.CENTRAL_SURFACES_COMMAND_QUEUE_CALLBACKS_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CENTRAL_SURFACES_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.FOOTER_ACTIONS_VIEW_BINDER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.KEYGUARD_STATUSBAR_VIEW_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NUM_PAD_KEY_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_9_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.QS_IMPL_CLASS
import com.mwilky.androidenhanced.Utils.Companion.isUnlocked
import com.mwilky.androidenhanced.xposed.QuicksettingsPremium.Companion.updateBatteryIconColors
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.newInstance
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setIntField
import de.robv.android.xposed.XposedHelpers.setObjectField


class Lockscreen {

    companion object {
        //Class Objects
        lateinit var KeyguardStatusBarView: Any
        var PowerMenuButton: Any? = null

        // Class references
        lateinit var `QSFooterView$$ExternalSyntheticLambda0`: Class<*>

        //Tweak Variables
        var mHideLockscreenStatusbarEnabled: Boolean = false
        var mScrambleKeypadEnabled: Boolean = false
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

            //Block power menu
            // Blocks power menu on power + volume button press
            findAndHookMethod(PHONE_WINDOW_MANAGER_9_CLASS,
                classLoader,
                "execute",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        val isScreenOn = callMethod(
                            getSurroundingThis(param.thisObject), "isScreenOn"
                        ) as Boolean

                        val keyguardOn = callMethod(
                            getSurroundingThis(param.thisObject), "keyguardOn"
                        ) as Boolean

                        val mPowerVolUpBehavior = getIntField(
                            getSurroundingThis(param.thisObject), "mPowerVolUpBehavior"
                        )
                        if (mPowerVolUpBehavior == 2) {
                            if (mDisableLockscreenPowerMenuEnabled && isScreenOn && keyguardOn) {
                                callMethod(
                                    getSurroundingThis(param.thisObject),
                                    "performHapticFeedback",
                                    10003,
                                    "Power + Volume Up - Global Actions Suppressed"
                                )
                                param.result = null
                            }
                        }
                    }
                })

            // Block power menu
            // Blocks power menu on a regular power button press
            findAndHookMethod(PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "powerLongPress",
                Long::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        val isScreenOn = callMethod(param.thisObject, "isScreenOn") as Boolean
                        val keyguardOn = callMethod(param.thisObject, "keyguardOn") as Boolean
                        val resolvedLongPressOnPowerBehavior = callMethod(
                            param.thisObject,
                            "getResolvedLongPressOnPowerBehavior"
                        ) as Int

                        if (resolvedLongPressOnPowerBehavior == 1) {
                            if (mDisableLockscreenPowerMenuEnabled && isScreenOn && keyguardOn) {
                                setBooleanField(param.thisObject, "mPowerKeyHandled", true)
                                callMethod(
                                    param.thisObject,
                                    "performHapticFeedback",
                                    10003,
                                    "Power - Long Press - Global Actions Suppressed"
                                )
                                param.result = null
                            }
                        }
                    }
                })

        }

        fun initSystemUI(classLoader: ClassLoader?) {

            // Class references
            val numPadKeyClass = findClass(
                NUM_PAD_KEY_CLASS, classLoader
            )

            //Disable power menu lockscreen
            `QSFooterView$$ExternalSyntheticLambda0` = findClass(
                "com.android.systemui.qs.QSFooterView$\$ExternalSyntheticLambda0", classLoader
            )

            //Scramble Keypad
            hookAllConstructors(numPadKeyClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (mScrambleKeypadEnabled) {
                        val mDigitText = getObjectField(param.thisObject, "mDigitText")
                        val mDigit = getIntField(param.thisObject, "mDigit")
                        setObjectField(param.thisObject, "mDigit", sNumbers[mDigit])
                        callMethod(mDigitText, "setText", sNumbers[mDigit].toString())
                    }
                }
            })

            //Hide lockscreen statusbar
            findAndHookMethod(KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "updateVisibilities",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        if (mHideLockscreenStatusbarEnabled) callMethod(
                            param.thisObject,
                            "setVisibility",
                            View.GONE
                        )

                        val mBatteryView = getObjectField(param.thisObject, "mBatteryView")

                        updateBatteryIconColors(mBatteryView, "KEYGUARD")

                    }
                })

            // Hide lockscreen statusbar
            findAndHookMethod(KEYGUARD_STATUSBAR_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        KeyguardStatusBarView = param.thisObject
                    }
                })

            //Scramble Keypad
            findAndHookMethod(KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS,
                classLoader,
                "onFinishInflate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (mScrambleKeypadEnabled) sNumbers.shuffle()
                    }
                })

            //Disable power menu lockscreen
            //TODO: this hasnt worked for a while, need to fix
            findAndHookMethod(CENTRAL_SURFACES_IMPL_CLASS,
                classLoader,
                "updateIsKeyguard",
                Boolean::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        val mKeyguardStateController =
                            getObjectField(param.thisObject, "mKeyguardStateController")

                        val mKeyguardShowing = getBooleanField(mKeyguardStateController, "mShowing")

                        //If on lockscreen and tweak is enabled, hide the button
                        if (PowerMenuButton != null) {
                            if (!mKeyguardShowing || !mDisableLockscreenPowerMenuEnabled) {
                                (PowerMenuButton as View).visibility = View.VISIBLE
                            } else {
                                (PowerMenuButton as View).visibility = View.GONE
                            }
                        }
                    }
                })

            //Disable QS on lockscreen
            findAndHookMethod(QS_IMPL_CLASS,
                classLoader,
                "disable",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        val mContext = callMethod(param.thisObject, "getContext") as Context
                        val displayId = param.args[0] as Int
                        var state2 = param.args[2] as Int

                        if (displayId != mContext.display!!.displayId) return null

                        val mContainer = getObjectField(param.thisObject, "mContainer")
                        val mHeader = getObjectField(param.thisObject, "mHeader")
                        val mFooter = getObjectField(param.thisObject, "mFooter")
                        val mFooterView = getObjectField(mFooter, "mView") as FrameLayout
                        val mQuickQSPanel = getObjectField(mHeader, "mHeaderQsPanel") as View

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

                        setBooleanField(mFooterView, "mQsDisabled", disabled)
                        mFooterView.post(
                            newInstance(
                                `QSFooterView$$ExternalSyntheticLambda0`,
                                mFooterView
                            ) as Runnable?
                        )

                        callMethod(param.thisObject, "updateQsState")

                        return null

                    }
                })

            //Disable QS on lockscreen
            findAndHookMethod(CENTRAL_SURFACES_COMMAND_QUEUE_CALLBACKS_CLASS,
                classLoader,
                "disable",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        val displayId = param.args[0] as Int
                        val state1 = param.args[1] as Int
                        var state2 = param.args[2] as Int

                        val mDisplayId = getIntField(param.thisObject, "mDisplayId")

                        if (displayId != mDisplayId) return null

                        val mRemoteInputQuickSettingsDisabler =
                            getObjectField(param.thisObject, "mRemoteInputQuickSettingsDisabler")
                        val mCentralSurfaces = getObjectField(param.thisObject, "mCentralSurfaces")


                        val mShadeHeaderController =
                            if (Build.VERSION.SDK_INT >= 35) {
                                getObjectField(param.thisObject, "mShadeHeaderController")
                            } else {
                                val mShadeViewController = getObjectField(param.thisObject, "mShadeViewController")
                                getObjectField(mShadeViewController, "mShadeHeaderController")
                            }

                        val mShadeController = getObjectField(param.thisObject, "mShadeController")
                        val mHeadsUpManager = getObjectField(param.thisObject, "mHeadsUpManager")
                        val mKeyguardStateController =
                            getObjectField(param.thisObject, "mKeyguardStateController")

                        state2 = adjustDisableFlags(mRemoteInputQuickSettingsDisabler, state2)
                        state2 = adjustQsDisableFlags(mKeyguardStateController, state2)

                        val old1 = getIntField(param.thisObject, "mDisabled1")

                        val diff1: Int = state1 xor old1

                        setIntField(param.thisObject, "mDisabled1", state1)

                        val old2 = getIntField(param.thisObject, "mDisabled2")

                        val diff2: Int = state2 xor old2

                        setIntField(param.thisObject, "mDisabled2", state2)

                        if (diff1 and DISABLE_EXPAND != 0) {
                            if (state1 and DISABLE_EXPAND != 0) {
                                callMethod(mShadeController, "animateCollapseShade", 0, false, false, 1.0f)
                            }
                        }

                        if (diff1 and DISABLE_NOTIFICATION_ALERTS != 0) {
                            if (state1 and DISABLE_NOTIFICATION_ALERTS != 0) {
                                callMethod(mHeadsUpManager, "releaseAllImmediately")
                            }
                        }

                        if (diff2 and DISABLE2_NOTIFICATION_SHADE != 0) {
                            if (state2 and DISABLE2_NOTIFICATION_SHADE != 0) {
                                callMethod(mShadeController, "animateCollapseShade", 0, false, false, 1.0f)
                            }
                        }

                        val disabled = state2 and DISABLE2_QUICK_SETTINGS != 0

                        val mShadeHeaderControllerQsDisabled =
                            getBooleanField(mShadeHeaderController, "qsDisabled")

                        if (disabled != mShadeHeaderControllerQsDisabled) {
                            setBooleanField(mShadeHeaderController, "qsDisabled", disabled)
                            callMethod(mShadeHeaderController, "updateVisibility$2")
                        }

                        return null

                    }
                })
        }


        // Additional functions
        // disable qs on lockscreen
        // Moved this functionality out of the disable functions so we didn't have to recreate it twice
        fun adjustDisableFlags(mRemoteInputQuickSettingsDisabler: Any, state: Int): Int {
            var mutableState = state
            val remoteInputActive =
                getBooleanField(mRemoteInputQuickSettingsDisabler, "remoteInputActive")
            val isLandscape = getBooleanField(mRemoteInputQuickSettingsDisabler, "isLandscape")
            val shouldUseSplitNotificationShade = getBooleanField(
                mRemoteInputQuickSettingsDisabler, "shouldUseSplitNotificationShade"
            )
            if (remoteInputActive && isLandscape && !shouldUseSplitNotificationShade) {
                mutableState = state or DISABLE2_QUICK_SETTINGS
            }
            return mutableState
        }

        //Sets flags based on if tweak is enabled and is on lockscreen
        private fun adjustQsDisableFlags(mKeyguardStateController: Any, state2: Int): Int {
            return if (mDisableLockscreenQuicksettingsEnabled && !isUnlocked(
                    mKeyguardStateController
                )
            ) {
                DISABLE2_QUICK_SETTINGS
            } else {
                state2
            }
        }
    }
}