package com.mwilky.androidenhanced.xposed
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.Message
import android.telecom.TelecomManager
import android.util.Log
import android.view.Display
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.mwilky.androidenhanced.BroadcastUtils.Companion.PREFS
import com.mwilky.androidenhanced.BroadcastUtils.Companion.registerBroadcastReceiver
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.doubleTapToSleepLauncher
import com.mwilky.androidenhanced.Utils.Companion.gestureSleep
import com.mwilky.androidenhanced.Utils.Companion.isTorchEnabled
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.setBooleanField
import de.robv.android.xposed.XposedHelpers.setIntField

class Buttons {

    companion object {
        //Hook Classes
        private const val PHONE_WINDOW_MANAGER_CLASS =
            "com.android.server.policy.PhoneWindowManager"
        private const val PhoneWindowManagerPowerKeyRuleClass =
            "com.android.server.policy.PhoneWindowManager\$PowerKeyRule"
        private const val PhoneWindowManagerPolicyHandlerClass =
            "com.android.server.policy.PhoneWindowManager\$PolicyHandler"
        private const val PowerManagerServiceClass =
            "com.android.server.power.PowerManagerService"
        private const val PowerManagerServicePowerManagerHandlerCallbackClass =
            "com.android.server.power.PowerManagerService\$PowerManagerHandlerCallback"
        private const val WindowManagerFuncsClass =
            "com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs"
        private const val MediaSessionLegacyHelperClass =
            "android.media.session.MediaSessionLegacyHelper"

        private const val WorkspaceTouchListener =
            "com.android.launcher3.touch.WorkspaceTouchListener"

        lateinit var PhoneWindowManagerObject: Any
        lateinit var utils: Any

        //Tweak Variables
        var mTorchPowerScreenOff: Boolean = false
        var mTorchAutoOff: Boolean = false
        var mVolKeyMedia = false

        var mDoubleTapSleepLauncherEnabled = false

        //Torch
        private const val MSG_TOGGLE_TORCH = 100
        private const val WAKE_REASON_LIFT = 16

        //Vol Key Media
        private const val MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK = 200
        private var mVolKeyLongPress = false
        private var MediaSessionLegacyHelper: Class<*>? = null
        private var PhoneWindowManagerClassReference: Class<*>? = null

        fun initLauncher(classLoader: ClassLoader?) {

            // Double tap launcher to sleep
            hookAllConstructors(
                findClass(WorkspaceTouchListener, classLoader),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        val mWorkspace = getObjectField(param.thisObject, "mWorkspace")
                        val mContext = callMethod(mWorkspace, "getContext")
                                as Context

                        registerBroadcastReceiver(
                            mContext,
                            doubleTapToSleepLauncher,
                            param.thisObject.toString(),
                            false
                        )

                        val sharedPreferences = mContext.getSharedPreferences(PREFS, MODE_PRIVATE)

                        mDoubleTapSleepLauncherEnabled = sharedPreferences.getBoolean(doubleTapToSleepLauncher, false)

                        log("mwilky: mDoubleTapSleepLauncherEnabled = $mDoubleTapSleepLauncherEnabled")

                        val mGestureDetector =
                            getObjectField(param.thisObject, "mGestureDetector")
                                    as GestureDetector

                        val onDoubleTapListener = object : GestureDetector.OnDoubleTapListener {
                            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                return false
                            }

                            override fun onDoubleTap(e: MotionEvent): Boolean {
                                if (mDoubleTapSleepLauncherEnabled) {
                                    sendDoubleTapBroadcast(mContext)
                                    return true
                                }
                                return false
                            }

                            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                                return false
                            }
                        }
                        mGestureDetector.setOnDoubleTapListener(onDoubleTapListener)
                    }
                })

        }

        fun init(classLoader: ClassLoader?) {

            //Init hooks
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
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
            //Vol Key Wake
            findAndHookMethod(
                PhoneWindowManagerPolicyHandlerClass,
                classLoader,
                "handleMessage",
                Message::class.java,
                handleMessage_hook_PhoneWindowManager
            )
            //Torch on power function
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "powerPress",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                powerPress_hook
            )

            //Torch on power function
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
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

            //Vol Key Media Control
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "interceptKeyBeforeQueueing",
                KeyEvent::class.java,
                Int::class.javaPrimitiveType,
                interceptKeyBeforeQueueing
            )

            //Vol Key Media Control
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "shouldDispatchInputWhenNonInteractive",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                shouldDispatchInputWhenNonInteractive
            )

            //MediaSessionLegacyHelper Class
            MediaSessionLegacyHelper =
                findClass(
                    MediaSessionLegacyHelperClass,
                    classLoader
                )
            //PhoneWindowManager Class
            PhoneWindowManagerClassReference =
                findClass(
                    PHONE_WINDOW_MANAGER_CLASS, classLoader
                )

        }

        //Hooked functions
        //Init hooks
        private val PhoneWindowManager_init_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                PhoneWindowManagerObject = param.thisObject

                val mContext: Context = getObjectField(param.thisObject,
                    "mContext")
                        as Context

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(mContext, torchPowerScreenOff,
                    param.thisObject.toString(),
                    false
                )
                registerBroadcastReceiver(mContext, volKeyMediaControl,
                    param.thisObject.toString(),
                    false
                )
                //Register this in this class so we don't need to rehook in Lockscreen.kt
                registerBroadcastReceiver(mContext, disableLockscreenPowerMenu,
                    param.thisObject.toString(),
                    false
                )
                //Register this in this class
                registerBroadcastReceiver(mContext, muteScreenOnNotifications,
                    param.thisObject.toString(),
                    false
                )

                val mHandler =
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

                val mHandler =
                    getObjectField(getSurroundingThis(param.thisObject), "mHandler")
                            as Handler

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
        //Vol Key Media Control
        //Handles the actions for each press
        private val handleMessage_hook_PhoneWindowManager: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val message = param.args[0] as Message

                if (message.what == MSG_TOGGLE_TORCH) {
                    (utils as Utils).toggleTorchMode()
                }

                if (message.what == MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK) {
                    val event = message.obj as KeyEvent
                    mVolKeyLongPress = true
                    dispatchMediaKeyWithWakeLockToAudioService(
                        event
                    )
                    dispatchMediaKeyWithWakeLockToAudioService(
                        KeyEvent.changeAction(event, 1)
                    )
                    callMethod(
                        getSurroundingThis(param.thisObject),
                        "performHapticFeedback", 0, false,
                        "Volume - Long Press - Media Control"
                    )
                }
            }
        }

        //Torch on power function
        //Wakes the device when tweak is enabled but is a short press
        private val powerPress_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val eventTime = param.args[0]
                        as Long

                val mSingleKeyGestureDetector =
                    getObjectField(param.thisObject, "mSingleKeyGestureDetector")

                val beganFromNonInteractive =
                    callMethod(mSingleKeyGestureDetector, "beganFromNonInteractive")
                        as Boolean

                if (mTorchPowerScreenOff && beganFromNonInteractive) {
                    callMethod(
                        param.thisObject,
                        "wakeUpFromWakeKey",
                        eventTime,
                        KeyEvent.KEYCODE_POWER,
                        false
                    )
                    param.result = null
                }
            }
        }

        //Torch on power function
        //Stops waking the device when the mod is enabled
        private val interceptPowerKeyDown_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

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

                var mPowerKeyHandled =
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
                                "wakeUpFromWakeKey",
                                event)
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
                val mHandler =
                    getObjectField(param.thisObject, "mHandler")
                            as Handler

                //Register broadcast receiver to receive values
                registerBroadcastReceiver(
                    mContext,
                    torchAutoOffScreenOn,
                    param.thisObject.toString(),
                    false
                )

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

                val wakeReason = param.args[1]

                val mHandler =
                    getObjectField(param.thisObject, "mHandler")
                            as Handler

                val recalculateGlobalWakefulnessLocked = callMethod(
                    param.thisObject,
                    "recalculateGlobalWakefulnessLocked"
                ) as Int

                if (recalculateGlobalWakefulnessLocked == 1) {
                    //Do not toggle torch if woken by lifting device
                    if (wakeReason != WAKE_REASON_LIFT) {
                        if (mTorchAutoOff && isTorchEnabled) {
                            mHandler.removeMessages(MSG_TOGGLE_TORCH)
                            val obtainMessage: Message = mHandler.obtainMessage(MSG_TOGGLE_TORCH)
                            obtainMessage.isAsynchronous = true
                            obtainMessage.sendToTarget()
                        }
                    }
                }
            }
        }

        private val interceptKeyBeforeQueueing: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val mContext: Context = getObjectField(param.thisObject, "mContext")
                        as Context

                val mHandler =
                    getObjectField(param.thisObject, "mHandler")
                            as Handler

                val isScreenOn = callMethod(param.thisObject, "isScreenOn") as Boolean

                val dreamManager: Any = callStaticMethod(
                    PhoneWindowManagerClassReference,
                    "getDreamManager"
                )

                //ONLY FUNCTION IS SCREEN IS OFF OR AOD
                if (!isScreenOn or callMethod(dreamManager, "isDreaming") as Boolean) {
                    val event = param.args[0] as KeyEvent

                    val policyFlags = param.args[1] as Int

                    val keyCode = event.keyCode

                    val down = event.action == KeyEvent.ACTION_DOWN

                    val interactive = policyFlags and 2 != 0

                    var isWakeKey = (policyFlags and 1 != 0
                            || callMethod(event, "isWakeKey") as Boolean)

                    val isNavBarVirtKey = event.flags and KeyEvent.FLAG_VIRTUAL_HARD_KEY != 0

                    val displayId = callMethod(event, "getDisplayId") as Int

                    val isInjected = policyFlags and 0x01000000 != 0

                    val useHapticFeedback =
                        down && policyFlags and 2 != 0 && (!isNavBarVirtKey || getBooleanField(
                            param.thisObject,
                            "mNavBarVirtualKeyHapticFeedbackEnabled"
                        )) && event.repeatCount == 0

                    val mediaSessionLegacyHelperObject = callStaticMethod(
                        MediaSessionLegacyHelper, "getHelper", mContext
                    )

                    // Basic policy based on interactive state.
                    var result: Int
                    if (interactive || isInjected && !isWakeKey) {
                        // When the device is interactive or the key is injected pass the
                        // key to the application.
                        result = 1
                        isWakeKey = false
                        if (interactive) {
                            // If the screen is awake, but the button pressed was the one
                            // that woke the device
                            // then don't pass it to the application
                            if (keyCode == getIntField(
                                    param.thisObject,
                                    "mPendingWakeKey"
                                ) && !down
                            ) {
                                result = 0
                            }
                            // Reset the pending key
                            setIntField(param.thisObject, "mPendingWakeKey",
                                -1)
                        }
                    } else if (callMethod(
                            param.thisObject,
                            "shouldDispatchInputWhenNonInteractive",
                            displayId,
                            keyCode
                        ) as Boolean
                    ) {
                        // If we're currently dozing with the screen on and the keyguard showing,
                        // pass the key to the application but preserve its wake key status to make
                        // sure we still move from dozing to fully interactive if we would normally
                        // go from off to fully interactive.
                        result = 1
                        // Since we're dispatching the input, reset the pending key
                        setIntField(param.thisObject, "mPendingWakeKey", -1)
                    } else {
                        // When the screen is off and the key is not injected, determine whether
                        // to wake the device but don't pass the key to the application.
                        result = 0
                        val isWakeKeyWhenScreenOff: Boolean =
                            callMethod(param.thisObject, "isWakeKeyWhenScreenOff",
                            keyCode)
                                as Boolean
                        if (isWakeKey && (!down || !isWakeKeyWhenScreenOff)
                        ) {
                            isWakeKey = false
                        }
                        // Cache the wake key on down event so we can also avoid sending the up
                        // event to the app
                        if (isWakeKey && down) {
                            setIntField(param.thisObject, "mPendingWakeKey",
                                keyCode)
                        }
                    }

                    // If the key would be handled globally, just return the result, don't worry
                    // about special
                    // key processing.
                    if (callMethod(param.thisObject, "isValidGlobalKey", keyCode)
                                as Boolean
                        && callMethod(
                            getObjectField(
                                param.thisObject,
                                "mGlobalKeyManager"
                            ), "shouldHandleGlobalKey", keyCode
                        ) as Boolean
                    ) {
                        // Dispatch if global key defined dispatchWhenNonInteractive.
                        if (!interactive && isWakeKey && down
                            && callMethod(
                                getObjectField(
                                    param.thisObject,
                                    "mGlobalKeyManager"
                                ), "shouldDispatchFromNonInteractive", keyCode
                            ) as Boolean
                        ) {
                            callMethod(
                                getObjectField(param.thisObject, "mGlobalKeyManager"),
                                "setBeganFromNonInteractive"
                            )
                            result = 1
                            // Since we're dispatching the input, reset the pending key
                            setIntField(param.thisObject, "mPendingWakeKey",
                                -1)
                        }
                        if (isWakeKey) {
                            callMethod(param.thisObject, "wakeUpFromWakeKey", event)
                        }
                        param.result = result
                    }
                    when (keyCode) {
                        KeyEvent.KEYCODE_VOLUME_UP,
                        KeyEvent.KEYCODE_VOLUME_DOWN,
                        KeyEvent.KEYCODE_VOLUME_MUTE -> {
                            if (down) {
                                callMethod(
                                    param.thisObject,
                                    "sendSystemKeyToStatusBarAsync", event
                                )
                                val nm = callMethod(
                                    param.thisObject,
                                    "getNotificationService"
                                ) as NotificationManager
                                if (!getBooleanField(
                                        param.thisObject,
                                        "mHandleVolumeKeysInWM"
                                    )
                                ) {
                                    callMethod(nm, "silenceNotificationSound")
                                }
                                val telecomManager = callMethod(
                                    param.thisObject,
                                    "getTelecommService"
                                ) as TelecomManager
                                if (!getBooleanField(
                                        param.thisObject,
                                        "mHandleVolumeKeysInWM"
                                    )
                                ) {
                                    // When {@link #mHandleVolumeKeysInWM} is set, volume key events
                                    // should be dispatched to WM.
                                    if (callMethod(
                                            telecomManager,
                                            "isRinging"
                                        ) as Boolean
                                    ) {
                                        // If an incoming call is ringing, either VOLUME key means
                                        // "silence ringer".  We handle these keys here, rather than
                                        // in the InCallScreen, to make sure we'll respond to them
                                        // even if the InCallScreen hasn't come to the foreground yet.
                                        // Look for the DOWN event here, to agree with the "fallback"
                                        // behavior in the InCallScreen.
                                        Log.i(
                                            "PhoneWindowManager", "interceptKeyBeforeQueueing:"
                                                    + " VOLUME key-down while ringing: Silence ringer!"
                                        )

                                        // Silence the ringer.  (It's safe to call this
                                        // even if the ringer has already been silenced.)
                                        callMethod(
                                            telecomManager,
                                            "silenceRinger"
                                        )

                                        // And *don't* pass this key thru to the current activity
                                        // (which is probably the InCallScreen.)
                                        //ACTION_PASS_TO_USER
                                        result = result and 1.inv()
                                    }
                                }
                                var audioMode = AudioManager.MODE_NORMAL
                                try {
                                    audioMode = callMethod(
                                        callMethod(param.thisObject, "getAudioService"),
                                        "getMode"
                                    ) as Int
                                } catch (e: Exception) {
                                    Log.e(
                                        "PhoneWindowManager", "Error getting AudioService" +
                                                " in interceptKeyBeforeQueueing.", e
                                    )
                                }
                                val isInCall = callMethod(
                                    telecomManager,
                                    "isInCall"
                                ) as Boolean || audioMode == AudioManager.MODE_IN_COMMUNICATION
                                if (isInCall && result and 1 == 0) {
                                    // If we are in call but we decided not to pass the key to
                                    // the application, just pass it to the session service.
                                    callMethod(
                                        mediaSessionLegacyHelperObject,
                                        "sendVolumeKeyEvent", event,
                                        AudioManager.USE_DEFAULT_STREAM_TYPE, false
                                    )
                                }
                            }
                            if (getBooleanField(param.thisObject, "mUseTvRouting")
                                || getBooleanField(
                                    param.thisObject,
                                    "mHandleVolumeKeysInWM"
                                ) || !mVolKeyMedia
                            ) {
                                // Defer special key handlings to
                                // {@link interceptKeyBeforeDispatching()}.
                                result = result or 1
                            } else if (result and 1 == 0) {
                                var mayChangeVolume = false
                                if (isMusicPlayingInActiveSessions(mContext)) {
                                    if (mVolKeyMedia && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
                                        // Detect long key presses.
                                        if (down) {
                                            mVolKeyLongPress = false
                                            val newKeyCode =
                                                if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                                                    KeyEvent.KEYCODE_MEDIA_NEXT
                                                else KeyEvent.KEYCODE_MEDIA_PREVIOUS

                                            scheduleLongPressKeyEvent(event, newKeyCode)
                                            // Consume key down events of all presses.
                                        } else {
                                            mHandler.removeMessages(
                                                MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK
                                            )
                                            // Consume key up events of long presses only.
                                            // Change volume only on key up events of short presses.
                                            if (!mVolKeyLongPress) {
                                                mayChangeVolume = true
                                            }

                                        }
                                    } else {
                                        // Long key press detection not applicable, change volume only
                                        // on key down events
                                        mayChangeVolume = down
                                    }
                                } else {
                                    result = result or 1
                                }
                                if (mayChangeVolume) {
                                    // If we aren't passing to the user and no one else
                                    // handled it send it to the session manager to figure
                                    // out.

                                    // Rewrite the event to use key-down as sendVolumeKeyEvent will
                                    // only change the volume on key down.
                                    val newEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
                                    callMethod(
                                        mediaSessionLegacyHelperObject,
                                        "sendVolumeKeyEvent", newEvent,
                                        AudioManager.USE_DEFAULT_STREAM_TYPE, true
                                    )
                                }
                            }
                        }
                    }
                    if (useHapticFeedback) {
                        callMethod(
                            param.thisObject, "performHapticFeedback",
                            HapticFeedbackConstants.VIRTUAL_KEY, false, "Virtual Key - Press"
                        )
                    }

                    if (!interactive && isWakeKey && down && callMethod(
                            getObjectField(param.thisObject, "mGlobalKeyManager"),
                            "shouldDispatchFromNonInteractive", keyCode
                        ) as Boolean
                    ) {
                        callMethod(
                            getObjectField(param.thisObject, "mGlobalKeyManager"),
                            "setBeganFromNonInteractive"
                        )
                        result = 1
                        setIntField(param.thisObject, "mPendingWakeKey",
                            -1)
                    }

                    if (isWakeKey) {
                        callMethod(param.thisObject, "wakeUpFromWakeKey", event)
                    }

                    if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE ||
                        keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                        keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        param.result = result
                    }
                }
            }
        }

        //This gets called when torch tweak broadcast received to set
        //mSupportLongPressPowerWhenNonInteractive back to default value when tweak is disabled
        fun updateSupportLongPressPowerWhenNonInteractive(value: Boolean) {
            setBooleanField(PhoneWindowManagerObject,
                "mSupportLongPressPowerWhenNonInteractive",
                value
            )
        }

        private fun dispatchMediaKeyWithWakeLockToAudioService(keyEvent: KeyEvent) {
            val mContext: Context = getObjectField(PhoneWindowManagerObject, "mContext")
                as Context
            val mediaSessionLegacyHelperObject = callStaticMethod(
                MediaSessionLegacyHelper,
                "getHelper",
                mContext
            )
            if (callMethod(
                    getObjectField(
                        PhoneWindowManagerObject,
                        "mActivityManagerInternal"
                    ), "isSystemReady"
                ) as Boolean
            ) {
                callMethod(mediaSessionLegacyHelperObject, "sendMediaButtonEvent",
                    keyEvent, true
                )
            }
        }

        private val shouldDispatchInputWhenNonInteractive: XC_MethodReplacement =
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    val displayId = param.args[0] as Int
                    val keyCode = param.args[1] as Int
                    val dreamManager: Any? = callStaticMethod(
                        PhoneWindowManagerClassReference,
                        "getDreamManager"
                    )
                    val z = displayId == 0 || displayId == -1
                    val display = if (z) getObjectField(
                        param.thisObject,
                        "mDefaultDisplay"
                    ) as Display else callMethod(
                        getObjectField(
                            param.thisObject,
                            "mDisplayManager"
                        ), "getDisplay", displayId
                    ) as Display
                    val z2 = display.state == 1
                    if (z2 && !getBooleanField(param.thisObject, "mHasFeatureWatch")) {
                        return false
                    }
                    var isDreaming = false
                    try {
                        val dreamManagerisDreaming =
                            callMethod(dreamManager, "isDreaming") as Boolean
                        if (dreamManager != null && dreamManagerisDreaming) {
                            isDreaming = true
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("PhoneWindowManager", "RemoteException when checking " +
                                "if dreaming", e)
                    }
                    if (isDreaming && isVolumeKey(
                            keyCode
                        )
                    ) {
                        // We don't want the volume key events to be dispatched when the system
                        // is deaming in order to process them later
                        return false
                    }
                    if (callMethod(
                            param.thisObject,
                            "isKeyguardShowingAndNotOccluded"
                        ) as Boolean && !z2
                    ) {
                        return true
                    }
                    // Watches handle BACK and hardware buttons specially
                    if (getBooleanField(
                            param.thisObject,
                            "mHasFeatureWatch"
                        ) && (keyCode == KeyEvent.KEYCODE_BACK ||
                                keyCode == KeyEvent.KEYCODE_STEM_PRIMARY ||
                                keyCode == KeyEvent.KEYCODE_STEM_1 ||
                                keyCode == KeyEvent.KEYCODE_STEM_2 ||
                                keyCode == KeyEvent.KEYCODE_STEM_3)
                    ) {
                        return false
                    }
                    if (z) {
                        // Send events to a dozing dream even if the screen is off since the dream
                        // is in control of the state of the screen.
                        if (isDreaming) {
                            return true
                        }
                    }
                    return false
                }
            }

        private fun isMusicPlayingInActiveSessions(context: Context): Boolean {
            val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val mediaControllers = mediaSessionManager.getActiveSessions(null)

            for (controller in mediaControllers) {
                val playbackState = controller.playbackState
                if (playbackState != null && playbackState.state == android.media.session.PlaybackState.STATE_PLAYING) {
                    return true
                }
            }
            return false
        }

        private fun isVolumeKey(code: Int): Boolean {
            return code == 25 || code == 24
        }

        private fun scheduleLongPressKeyEvent(origEvent: KeyEvent, keyCode: Int) {
            val mHandler =
                getObjectField(PhoneWindowManagerObject, "mHandler")
                        as Handler
            val event = KeyEvent(
                origEvent.downTime, origEvent.eventTime,
                origEvent.action, keyCode, 0
            )
            val msg: Message =
                mHandler.obtainMessage(
                    MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK,
                    event
                )
            msg.isAsynchronous = true
            mHandler.sendMessageDelayed(
                msg,
                ViewConfiguration.getLongPressTimeout().toLong()
            )
        }

        private fun sendDoubleTapBroadcast(context: Context) {
            val intent = Intent()
            intent.action = gestureSleep

            context.sendBroadcast(intent)
        }
    }
}