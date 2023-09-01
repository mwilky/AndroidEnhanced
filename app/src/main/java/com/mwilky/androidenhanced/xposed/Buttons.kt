package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log
import android.view.KeyEvent
import com.mwilky.androidenhanced.BroadcastUtils.Companion.registerBroadcastReceiver
import com.mwilky.androidenhanced.MainActivity.Companion.DEBUG
import com.mwilky.androidenhanced.MainActivity.Companion.TAG
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.isTorchEnabled
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.setBooleanField

class Buttons {

    companion object {
        private const val PhoneWindowManagerClass =
            "com.android.server.policy.PhoneWindowManager"
        private const val WindowManagerFuncsClass =
            "com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs"
        private const val PhoneWindowManagerPowerKeyRuleClass =
            "com.android.server.policy.PhoneWindowManager\$PowerKeyRule"
        private const val PhoneWindowManagerPolicyHandlerClass =
            "com.android.server.policy.PhoneWindowManager\$PolicyHandler"
        private const val PowerManagerServiceClass =
            "com.android.server.power.PowerManagerService"
        private const val PowerManagerServicePowerManagerHandlerCallbackClass =
            "com.android.server.power.PowerManagerService\$PowerManagerHandlerCallback"

        lateinit var mHandler: Handler
        lateinit var PhoneWindowManager: Any
        lateinit var utils: Any

        //Tweak Variables
        var mTorchPowerScreenOff: Boolean = false
        var mTorchAutoOff: Boolean = false

        //Torch
        private const val MSG_TOGGLE_TORCH = 25
        private var mPowerKeyHandled = false

        fun init(classLoader: ClassLoader?) {

            //Init hooks
            findAndHookMethod(
                PhoneWindowManagerClass,
                classLoader,
                "init",
                Context::class.java,
                WindowManagerFuncsClass,
                PhoneWindowManager_init_hook
            )


            //Init hooks
            findAndHookMethod(
                PowerManagerServiceClass,
                classLoader,
                "systemReady",
                systemReady_hook
            )

            //Torch on power function
            findAndHookMethod(
                PhoneWindowManagerPowerKeyRuleClass,
                classLoader,
                "onLongPress",
                Long::class.javaPrimitiveType,
                onLongPress_replacement
            )

            //Torch on power function
            findAndHookMethod(
                PhoneWindowManagerPolicyHandlerClass,
                classLoader,
                "handleMessage",
                Message::class.java,
                handleMessage_hook_PhoneWindowManager
            )

            //Torch on power function
            findAndHookMethod(
                PhoneWindowManagerClass,
                classLoader,
                "powerPress",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                powerPress_hook
            )

            //Torch on power function
            findAndHookMethod(
                PhoneWindowManagerClass,
                classLoader,
                "interceptPowerKeyDown",
                KeyEvent::class.java,
                Boolean::class.javaPrimitiveType,
                interceptPowerKeyDown_hook
            )

            //Torch auto off function
            findAndHookMethod(
                PowerManagerServiceClass,
                classLoader,
                "updateGlobalWakefulnessLocked",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
                String::class.java,
                updateGlobalWakefulnessLocked_hook
            )

            //Torch auto off function
            findAndHookMethod(
                PowerManagerServicePowerManagerHandlerCallbackClass,
                classLoader,
                "handleMessage",
                Message::class.java,
                handleMessage_hook_PowerManagerService
            )

        }

        //Hooked functions
        //Init hooks
        private val PhoneWindowManager_init_hook: XC_MethodHook = object : XC_MethodHook() {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            override fun afterHookedMethod(param: MethodHookParam) {
                if (DEBUG) log("$TAG: hooked ${param.thisObject}")
                PhoneWindowManager = param.thisObject
                val mContext: Context = getObjectField(PhoneWindowManager, "mContext")
                        as Context

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(mContext, torchPowerScreenOff,
                    param.thisObject.toString()
                )

                mHandler =
                    getObjectField(param.thisObject, "mHandler")
                            as Handler

                //Initialise Utils + torch callbacks
                utils = Utils(mContext, mHandler)
                (utils as Utils).registerTorchCallback()
            }
        }

        //Torch on power function
        //Toggles torch if enabled and started from non interactive
        private val onLongPress_replacement: XC_MethodHook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                if (callMethod(
                        getObjectField(
                            getSurroundingThis(param.thisObject),
                            "mSingleKeyGestureDetector"
                        ),
                        "beganFromNonInteractive"
                    ) as Boolean
                ) {
                    if (mTorchPowerScreenOff) {
                        mHandler.removeMessages(
                            MSG_TOGGLE_TORCH
                        )
                        val obtainMessage: Message =
                            mHandler.obtainMessage(
                                MSG_TOGGLE_TORCH
                            )
                        obtainMessage.isAsynchronous = true
                        obtainMessage.sendToTarget()
                        callMethod(
                            getSurroundingThis(param.thisObject),
                            "performHapticFeedback", 0, false,
                            "Power - Long Press - Torch"
                        )
                        return null
                    }
                    if (!getBooleanField(
                            getSurroundingThis(param.thisObject),
                            "mSupportLongPressPowerWhenNonInteractive"
                        )
                    ) {
                        Log.v(
                            "WindowManager",
                            "Not support long press power when device is not interactive."
                        )
                        return null
                    }
                }
                callMethod(
                    getSurroundingThis(param.thisObject),
                    "powerLongPress", param.args[0]
                )
                return null
            }
        }

        //Torch on power function
        //Handles the actions for each press
        private val handleMessage_hook_PhoneWindowManager: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val message = param.args[0] as Message
                if (message.what == MSG_TOGGLE_TORCH) {
                    (utils as Utils).toggleTorchMode()
                }
                /*if (message.what == com.android.mwilky.ricexposed.classes.PhoneWindowManager.MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK) {
                    val event = message.obj as KeyEvent
                    com.android.mwilky.ricexposed.classes.PhoneWindowManager.mVolKeyLongPress = true
                    com.android.mwilky.ricexposed.classes.PhoneWindowManager.dispatchMediaKeyWithWakeLockToAudioService(
                        event
                    )
                    com.android.mwilky.ricexposed.classes.PhoneWindowManager.dispatchMediaKeyWithWakeLockToAudioService(
                        KeyEvent.changeAction(event, 1)
                    )
                    callMethod(
                        getSurroundingThis(param.thisObject),
                        "performHapticFeedback", 0, false,
                        "Volume - Long Press - Media Control"
                    )
                }*/
            }
        }

        //Torch on power function
        //Wakes the device when tweak is enabled but is a short press
        private val powerPress_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val eventTime = param.args[0]
                        as Long
                val beganFromNonInteractive = param.args[2]
                        as Boolean
                if (mTorchPowerScreenOff && beganFromNonInteractive) {
                    callMethod(param.thisObject, "wakeUpFromPowerKey", eventTime)
                    param.result = null
                }
            }
        }

        //Torch on power function
        //Stops waking the device when the mod is enabled
        private val interceptPowerKeyDown_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                //Parameters
                val event = param.args[0]
                        as KeyEvent
                val interactive = param.args[1]
                        as Boolean
                val mIncallPowerBehavior =
                    getIntField(param.thisObject, "mIncallPowerBehavior")
                val telecomManager =
                    callMethod(param.thisObject, "getTelecommService")
                            as TelecomManager
                var hungUp = false
                if (callMethod(telecomManager, "isRinging")
                            as Boolean) {
                    callMethod(telecomManager, "silenceRinger")
                } else if (mIncallPowerBehavior and 2 != 0 && callMethod(
                        telecomManager,
                        "isInCall"
                    ) as Boolean && interactive
                ) {
                    hungUp = callMethod(telecomManager, "endCall") as Boolean
                }
                val handledByPowerManager = callMethod(
                    getObjectField(param.thisObject, "mPowerManagerInternal"),
                    "interceptPowerKeyDown", event
                ) as Boolean
                mPowerKeyHandled =
                    getBooleanField(param.thisObject, "mPowerKeyHandled")

                mPowerKeyHandled =
                    (mPowerKeyHandled || hungUp
                            || handledByPowerManager || callMethod(
                        getObjectField(
                            param.thisObject,
                            "mKeyCombinationManager"
                        ),
                        "isPowerKeyIntercepted"
                    ) as Boolean)
                if (!mPowerKeyHandled) {
                    if (!interactive) {
                        if (!mTorchPowerScreenOff) {
                            callMethod(param.thisObject,
                                "wakeUpFromPowerKey",
                                event.downTime)
                        }
                        param.result = null
                    }
                }
            }
        }

        //Torch auto off function
        //initialise everything
        private val systemReady_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mContext =
                    getObjectField(param.thisObject, "mContext")
                            as Context
                mHandler =
                    getObjectField(param.thisObject, "mHandler")
                            as Handler

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(mContext, torchAutoOffScreenOn,  param.thisObject.toString())

                //Initialise Utils + torch callbacks
                utils = Utils(mContext, mHandler)
                (utils as Utils).registerTorchCallback()
            }
        }

        //Torch auto off function
        //Do the toggle
        private val handleMessage_hook_PowerManagerService: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val message = param.args[0] as Message
                if (message.what == MSG_TOGGLE_TORCH) {
                    (utils as Utils).toggleTorchMode()
                }
            }
        }

        //Torch auto off function
        //Send the toggle if tweak enabled and torch is on
        private val updateGlobalWakefulnessLocked_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val recalculateGlobalWakefulnessLocked = callMethod(
                    param.thisObject,
                    "recalculateGlobalWakefulnessLocked"
                ) as Int
                if (recalculateGlobalWakefulnessLocked == 1) {
                    if (mTorchAutoOff && isTorchEnabled) {
                        mHandler.removeMessages(MSG_TOGGLE_TORCH)
                        val obtainMessage: Message = mHandler.obtainMessage(MSG_TOGGLE_TORCH)
                        obtainMessage.isAsynchronous = true
                        obtainMessage.sendToTarget()
                    }
                }
            }
        }

        //This gets called when torch tweak broadcast received to set
        //mSupportLongPressPowerWhenNonInteractive back to default value when tweak is disabled
        fun updateSupportLongPressPowerWhenNonInteractive(value: Boolean) {
            setBooleanField(PhoneWindowManager,
                "mSupportLongPressPowerWhenNonInteractive",
                value
            )
        }
    }
}