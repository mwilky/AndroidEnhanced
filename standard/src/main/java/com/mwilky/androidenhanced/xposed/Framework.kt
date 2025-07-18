package com.mwilky.androidenhanced.xposed

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.Message
import android.telecom.TelecomManager
import android.util.Log
import android.view.Display
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.mwilky.androidenhanced.HookedClasses.Companion.DISPLAY_ROTATION_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.GESTURE_LAUNCHER_SERVICE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.MEDIA_SESSIONS_LEGACY_HELPER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_ATTENTION_HELPER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_RECORD_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_9_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_POLICY_HANDLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_WINDOW_MANAGER_POWER_KEY_RULE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.POWER_MANAGER_SERVICE_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.POWER_MANAGER_SERVICE_POWER_MANAGER_HANDLER_CALLBACK_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.WINDOW_STATE_CLASS
import com.mwilky.androidenhanced.References.Companion.DisplayRotation
import com.mwilky.androidenhanced.References.Companion.MediaSessionLegacyHelper
import com.mwilky.androidenhanced.References.Companion.PhoneWindowManager
import com.mwilky.androidenhanced.References.Companion.ServicesContext
import com.mwilky.androidenhanced.References.Companion.SystemUIContext
import com.mwilky.androidenhanced.TorchHolder
import com.mwilky.androidenhanced.Utils.Companion.MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK
import com.mwilky.androidenhanced.Utils.Companion.MSG_TOGGLE_TORCH
import com.mwilky.androidenhanced.Utils.Companion.allowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.disableCameraScreenOff
import com.mwilky.androidenhanced.Utils.Companion.disableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.disableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.mAllowAllRotations
import com.mwilky.androidenhanced.Utils.Companion.mDisableCameraGestureWhenLocked
import com.mwilky.androidenhanced.Utils.Companion.mDisableLockscreenPowerMenu
import com.mwilky.androidenhanced.Utils.Companion.mDisableSecureScreenshots
import com.mwilky.androidenhanced.Utils.Companion.mMuteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.mTorchAutoOff
import com.mwilky.androidenhanced.Utils.Companion.mTorchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.mVolKeyMedia
import com.mwilky.androidenhanced.Utils.Companion.muteScreenOnNotifications
import com.mwilky.androidenhanced.Utils.Companion.safeHookAllConstructors
import com.mwilky.androidenhanced.Utils.Companion.safeHookMethod
import com.mwilky.androidenhanced.Utils.Companion.sendLogBroadcast
import com.mwilky.androidenhanced.Utils.Companion.shouldAutoTurnOffTorch
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnApplication
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnBiometric
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnCameraLaunch
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnGesture
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnLift
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnOther
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPlugIn
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnPowerButton
import com.mwilky.androidenhanced.Utils.Companion.torchAutoOffScreenOnTap
import com.mwilky.androidenhanced.Utils.Companion.torchPowerScreenOff
import com.mwilky.androidenhanced.Utils.Companion.volKeyMediaControl
import com.mwilky.androidenhanced.xposed.BroadcastReceiver.Companion.registerBroadcastReceiver
import de.robv.android.xposed.XC_MethodHook
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
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Framework {

    companion object {

        private var mVolKeyLongPress = false

        fun init(lpparam: LoadPackageParam) {
            val classLoader = lpparam.classLoader

            // Register broadcast receivers
            findAndHookMethod(
                PHONE_WINDOW_MANAGER_CLASS, classLoader, "systemBooted", object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {

                        ServicesContext = getObjectField(
                            param.thisObject, "mContext"
                        ) as Context

                        sendLogBroadcast(
                            ServicesContext,
                            "Hook Info",
                            "${ServicesContext.packageName} hooked successfully!"
                        )

                        MediaSessionLegacyHelper = findClass(
                            MEDIA_SESSIONS_LEGACY_HELPER_CLASS, classLoader
                        )

                        PhoneWindowManager = param.thisObject

                        // Register torch controller
                        TorchHolder.ensure(ServicesContext)

                        // Register broadcast receiver to receive values
                        registerBroadcastReceiver(
                            ServicesContext,
                            disableSecureScreenshots,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            ServicesContext, torchPowerScreenOff, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            ServicesContext, volKeyMediaControl, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            ServicesContext, allowAllRotations, param.thisObject.toString(), false
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOn,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            muteScreenOnNotifications,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            disableLockscreenPowerMenu,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            disableCameraScreenOff,
                            param.thisObject.toString(),
                            false
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnLift,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnBiometric,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnPlugIn,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnApplication,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnPowerButton,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnTap,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnCameraLaunch,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnGesture,
                            param.thisObject.toString(),
                            true
                        )

                        registerBroadcastReceiver(
                            ServicesContext,
                            torchAutoOffScreenOnOther,
                            param.thisObject.toString(),
                            true
                        )

                        // Functions purely for setting references
                        safeHookAllConstructors(
                            context = ServicesContext,
                            DISPLAY_ROTATION_CLASS,
                            classLoader,
                            afterHook = { param ->
                                DisplayRotation = param.thisObject
                            })

                        // Hooks
                        secureScreenshotsHooks(classLoader)
                        allowAllRotationsHooks(classLoader)
                        buttonHooks(classLoader)
                        muteScreenOnNotificationsHooks(classLoader)
                        disableLockscreenPowerMenuHooks(classLoader)
                        disableCameraScreenOffHooks(classLoader)
                    }
                })
        }

        /**
         * Hook functions
         */
        fun secureScreenshotsHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = ServicesContext,
                WINDOW_STATE_CLASS,
                classLoader,
                "isSecureLocked",
                afterHook = { param ->
                    val result = param.result as Boolean
                    if (result) {
                        if (mDisableSecureScreenshots) {
                            param.result = false
                        }
                    }
                })
        }

        fun disableLockscreenPowerMenuHooks(classLoader: ClassLoader) {
            // This handles regular power press
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "powerLongPress",
                Long::class.javaPrimitiveType,
                beforeHook = { param ->
                    val isScreenOn = callMethod(param.thisObject, "isScreenOn") as Boolean
                    val keyguardOn = callMethod(param.thisObject, "keyguardOn") as Boolean
                    val resolvedLongPressOnPowerBehavior = callMethod(
                        param.thisObject, "getResolvedLongPressOnPowerBehavior"
                    ) as Int

                    if (resolvedLongPressOnPowerBehavior == 1) {
                        if (mDisableLockscreenPowerMenu && isScreenOn && keyguardOn) {
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
                })

            // This handles power + volume press
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_9_CLASS,
                classLoader,
                "execute",
                beforeHook = { param ->
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
                        if (mDisableLockscreenPowerMenu && isScreenOn && keyguardOn) {
                            callMethod(
                                getSurroundingThis(param.thisObject),
                                "performHapticFeedback",
                                10003,
                                "Power + Volume Up - Global Actions Suppressed"
                            )
                            param.result = null
                        }
                    }
                })
        }

        fun disableCameraScreenOffHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = ServicesContext,
                GESTURE_LAUNCHER_SERVICE_CLASS,
                classLoader,
                "handleCameraGesture",
                Boolean::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val useWakeLock = param.args[0] as Boolean
                    val source = param.args[1] as Int

                    val mWindowManagerInternal =
                        getObjectField(param.thisObject, "mWindowManagerInternal")
                    val isKeyguardLocked =
                        callMethod(mWindowManagerInternal, "isKeyguardLocked") as Boolean

                    if (!useWakeLock && source == 1 && mDisableCameraGestureWhenLocked && isKeyguardLocked) {
                        param.result = false
                    }
                })
        }

        fun allowAllRotationsHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = ServicesContext,
                DISPLAY_ROTATION_CLASS,
                classLoader,
                "getAllowAllRotations",
                afterHook = { param ->
                    DisplayRotation = param.thisObject

                    val intResult: Int = if (mAllowAllRotations) 1 else 0

                    setIntField(param.thisObject, "mAllowAllRotations", intResult)

                    param.result = intResult
                })
        }

        fun muteScreenOnNotificationsHooks(classLoader: ClassLoader) {
            safeHookMethod(
                context = ServicesContext,
                NOTIFICATION_ATTENTION_HELPER_CLASS,
                classLoader,
                "buzzBeepBlinkLocked",
                findClass(NOTIFICATION_RECORD_CLASS, classLoader),
                findClass("$NOTIFICATION_ATTENTION_HELPER_CLASS\$Signals", classLoader),
                beforeHook = { param ->
                    val mScreenOn = getBooleanField(param.thisObject, "mScreenOn")
                    val mSystemReady = getBooleanField(param.thisObject, "mSystemReady")
                    val mAudioManager = getObjectField(param.thisObject, "mAudioManager")
                    val skipSound =
                        mScreenOn && mMuteScreenOnNotifications && mSystemReady && mAudioManager != null

                    if (skipSound) param.result = 0
                })
        }

        // This handles both power button and volume key hooks
        fun buttonHooks(classLoader: ClassLoader) {
            // Toggles torch if enabled and started from non interactive
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_POWER_KEY_RULE_CLASS,
                classLoader,
                "onLongPress",
                Long::class.javaPrimitiveType,
                replaceWith = lambda@{ param ->
                    val mHandler = getObjectField(
                        getSurroundingThis(param.thisObject), "mHandler"
                    ) as Handler

                    if (callMethod(
                            getObjectField(
                                getSurroundingThis(param.thisObject), "mSingleKeyGestureDetector"
                            ), "beganFromNonInteractive"
                        ) as Boolean
                    ) {
                        if (mTorchPowerScreenOff) {
                            mHandler.removeMessages(
                                MSG_TOGGLE_TORCH
                            )
                            val obtainMessage: Message = mHandler.obtainMessage(
                                MSG_TOGGLE_TORCH
                            )
                            obtainMessage.isAsynchronous = true
                            obtainMessage.sendToTarget()
                            callMethod(
                                getSurroundingThis(param.thisObject),
                                "performHapticFeedback",
                                0,
                                "Power - Long Press - Torch"
                            )
                            return@lambda null
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
                            return@lambda null
                        }
                    }
                    callMethod(
                        getSurroundingThis(param.thisObject), "powerLongPress", param.args[0]
                    )
                    return@lambda null
                })


            // Handle the button actions
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_POLICY_HANDLER_CLASS,
                classLoader,
                "handleMessage",
                Message::class.java,
                afterHook = { param ->
                    val message = param.args[0] as Message
                    when (message.what) {
                        MSG_TOGGLE_TORCH -> TorchHolder.toggle()
                        MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK -> {
                            val event = message.obj as KeyEvent
                            mVolKeyLongPress = true
                            dispatchMediaKeyWithWakeLockToAudioService(event)
                            dispatchMediaKeyWithWakeLockToAudioService(
                                KeyEvent.changeAction(
                                    event, 1
                                )
                            )
                        }
                    }
                })

            // Torch on power
            // Wake the device when the tweak is enabled but is short press
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "powerPress",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val eventTime = param.args[0] as Long

                    val mSingleKeyGestureDetector =
                        getObjectField(param.thisObject, "mSingleKeyGestureDetector")

                    val beganFromNonInteractive = callMethod(
                        mSingleKeyGestureDetector, "beganFromNonInteractive"
                    ) as Boolean

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
                })

            // Torch on power
            // Prevents waking the device when using the long press
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "interceptPowerKeyDown",
                KeyEvent::class.java,
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                beforeHook = { param ->
                    val event = param.args[0] as KeyEvent

                    val interactive = param.args[1] as Boolean

                    val mIncallPowerBehavior = getIntField(param.thisObject, "mIncallPowerBehavior")

                    val telecomManager = callMethod(
                        param.thisObject, "getTelecommService"
                    ) as TelecomManager

                    var hungUp = false

                    if (callMethod(telecomManager, "isRinging") as Boolean) {
                        callMethod(telecomManager, "silenceRinger")
                    } else if (mIncallPowerBehavior and 2 != 0 && callMethod(
                            telecomManager, "isInCall"
                        ) as Boolean && interactive
                    ) {
                        hungUp = callMethod(telecomManager, "endCall") as Boolean
                    }

                    val handledByPowerManager = callMethod(
                        getObjectField(param.thisObject, "mPowerManagerInternal"),
                        "interceptPowerKeyDown",
                        event
                    ) as Boolean

                    var mPowerKeyHandled = getBooleanField(param.thisObject, "mPowerKeyHandled")

                    mPowerKeyHandled =
                        (mPowerKeyHandled || hungUp || handledByPowerManager || callMethod(
                            getObjectField(
                                param.thisObject, "mKeyCombinationManager"
                            ), "isPowerKeyIntercepted"
                        ) as Boolean)

                    if (!mPowerKeyHandled) {
                        if (!interactive) {
                            if (!mTorchPowerScreenOff) {
                                callMethod(
                                    param.thisObject, "wakeUpFromWakeKey", event
                                )
                            }
                            param.result = null
                        }
                    }
                })

            // Auto Torch off
            safeHookMethod(
                context = ServicesContext,
                POWER_MANAGER_SERVICE_CLASS,
                classLoader,
                "updateGlobalWakefulnessLocked",
                Long::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
                String::class.java,
                beforeHook = { param ->
                    val wakeReason = param.args[1] as Int

                    val mHandler = getObjectField(param.thisObject, "mHandler") as Handler

                    val recalculateGlobalWakefulnessLocked = callMethod(
                        param.thisObject, "recalculateGlobalWakefulnessLocked"
                    ) as Int

                    if (recalculateGlobalWakefulnessLocked == 1) {
                        if (mTorchAutoOff && TorchHolder.isOn) {
                            // Check wake reason
                            if (shouldAutoTurnOffTorch(wakeReason)) {
                                mHandler.removeMessages(MSG_TOGGLE_TORCH)
                                val obtainMessage: Message =
                                    mHandler.obtainMessage(MSG_TOGGLE_TORCH)
                                obtainMessage.isAsynchronous = true
                                obtainMessage.sendToTarget()
                            }
                        }
                    }
                })

            // Auto Torch off
            safeHookMethod(
                context = ServicesContext,
                POWER_MANAGER_SERVICE_POWER_MANAGER_HANDLER_CALLBACK_CLASS,
                classLoader,
                "handleMessage",
                Message::class.java,
                afterHook = { param ->
                    val message = param.args[0] as Message

                    if (message.what == MSG_TOGGLE_TORCH) {
                        TorchHolder.toggle()
                    }
                })


            // Vol key media
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "shouldDispatchInputWhenNonInteractive",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                replaceWith = lambda@{ param ->
                    val displayId = param.args[0] as Int
                    val keyCode = param.args[1] as Int
                    val dreamManager: Any? = callStaticMethod(
                        findClass(PHONE_WINDOW_MANAGER_CLASS, classLoader), "getDreamManager"
                    )
                    val z = displayId == 0 || displayId == -1
                    val display = if (z) getObjectField(
                        param.thisObject, "mDefaultDisplay"
                    ) as Display else callMethod(
                        getObjectField(
                            param.thisObject, "mDisplayManager"
                        ), "getDisplay", displayId
                    ) as Display
                    val z2 = display.state == 1
                    if (z2 && !getBooleanField(param.thisObject, "mHasFeatureWatch")) {
                        return@lambda false
                    }
                    var isDreaming = false
                    try {
                        val dreamManagerisDreaming =
                            callMethod(dreamManager, "isDreaming") as Boolean
                        if (dreamManager != null && dreamManagerisDreaming) {
                            isDreaming = true
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e(
                            "PhoneWindowManager",
                            "RemoteException when checking " + "if dreaming",
                            e
                        )
                    }
                    if (isDreaming && isVolumeKey(
                            keyCode
                        )
                    ) {
                        // We don't want the volume key events to be dispatched when the system
                        // is deaming in order to process them later
                        return@lambda false
                    }
                    if (callMethod(
                            param.thisObject, "isKeyguardShowingAndNotOccluded"
                        ) as Boolean && !z2
                    ) {
                        return@lambda true
                    }
                    // Watches handle BACK and hardware buttons specially
                    if (getBooleanField(
                            param.thisObject, "mHasFeatureWatch"
                        ) && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_STEM_PRIMARY || keyCode == KeyEvent.KEYCODE_STEM_1 || keyCode == KeyEvent.KEYCODE_STEM_2 || keyCode == KeyEvent.KEYCODE_STEM_3)
                    ) {
                        return@lambda false
                    }
                    if (z) {
                        // Send events to a dozing dream even if the screen is off since the dream
                        // is in control of the state of the screen.
                        if (isDreaming) {
                            return@lambda true
                        }
                    }
                    return@lambda false
                })


            // Vol key media
            safeHookMethod(
                context = ServicesContext,
                PHONE_WINDOW_MANAGER_CLASS,
                classLoader,
                "interceptKeyBeforeQueueing",
                KeyEvent::class.java,
                Int::class.javaPrimitiveType,
                beforeHook = { param ->
                    val mContext: Context = getObjectField(param.thisObject, "mContext") as Context

                    val mHandler = getObjectField(param.thisObject, "mHandler") as Handler

                    val isScreenOn = callMethod(param.thisObject, "isScreenOn") as Boolean

                    val dreamManager: Any = callStaticMethod(
                        findClass(PHONE_WINDOW_MANAGER_CLASS, classLoader), "getDreamManager"
                    )

                    //ONLY FUNCTION IS SCREEN IS OFF OR AOD
                    if (!isScreenOn or callMethod(
                            dreamManager, "isDreaming"
                        ) as Boolean
                    ) {
                        val event = param.args[0] as KeyEvent
                        val policyFlags = param.args[1] as Int
                        val keyCode = event.keyCode
                        val down = event.action == KeyEvent.ACTION_DOWN
                        val interactive = policyFlags and 2 != 0
                        var isWakeKey = (policyFlags and 1 != 0 || callMethod(
                            event, "isWakeKey"
                        ) as Boolean)

                        val isNavBarVirtKey = event.flags and KeyEvent.FLAG_VIRTUAL_HARD_KEY != 0
                        val displayId = callMethod(event, "getDisplayId") as Int
                        val isInjected = policyFlags and 0x01000000 != 0

                        val useHapticFeedback =
                            down && policyFlags and 2 != 0 && (!isNavBarVirtKey || getBooleanField(
                                param.thisObject, "mNavBarVirtualKeyHapticFeedbackEnabled"
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
                                        param.thisObject, "mPendingWakeKey"
                                    ) && !down
                                ) {
                                    result = 0
                                }
                                // Reset the pending key
                                setIntField(
                                    param.thisObject, "mPendingWakeKey", -1
                                )
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
                            val isWakeKeyWhenScreenOff: Boolean = callMethod(
                                param.thisObject, "isWakeKeyWhenScreenOff", keyCode
                            ) as Boolean
                            if (isWakeKey && (!down || !isWakeKeyWhenScreenOff)) {
                                isWakeKey = false
                            }
                            // Cache the wake key on down event so we can also avoid sending the up
                            // event to the app
                            if (isWakeKey && down) {
                                setIntField(
                                    param.thisObject, "mPendingWakeKey", keyCode
                                )
                            }
                        }

                        // If the key would be handled globally, just return the result, don't worry
                        // about special
                        // key processing.
                        if (callMethod(
                                param.thisObject, "isValidGlobalKey", keyCode
                            ) as Boolean && callMethod(
                                getObjectField(
                                    param.thisObject, "mGlobalKeyManager"
                                ), "shouldHandleGlobalKey", keyCode
                            ) as Boolean
                        ) {
                            // Dispatch if global key defined dispatchWhenNonInteractive.
                            if (!interactive && isWakeKey && down && callMethod(
                                    getObjectField(
                                        param.thisObject, "mGlobalKeyManager"
                                    ), "shouldDispatchFromNonInteractive", keyCode
                                ) as Boolean
                            ) {
                                callMethod(
                                    getObjectField(
                                        param.thisObject, "mGlobalKeyManager"
                                    ), "setBeganFromNonInteractive"
                                )
                                result = 1
                                // Since we're dispatching the input, reset the pending key
                                setIntField(
                                    param.thisObject, "mPendingWakeKey", -1
                                )
                            }
                            if (isWakeKey) {
                                callMethod(param.thisObject, "wakeUpFromWakeKey", event)
                            }
                            param.result = result
                        }
                        when (keyCode) {
                            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_MUTE -> {
                                if (down) {
                                    callMethod(
                                        param.thisObject, "sendSystemKeyToStatusBarAsync", event
                                    )
                                    val nm = callMethod(
                                        param.thisObject, "getNotificationService"
                                    ) as NotificationManager
                                    if (!getBooleanField(
                                            param.thisObject, "mHandleVolumeKeysInWM"
                                        )
                                    ) {
                                        callMethod(nm, "silenceNotificationSound")
                                    }
                                    val telecomManager = callMethod(
                                        param.thisObject, "getTelecommService"
                                    ) as TelecomManager
                                    if (!getBooleanField(
                                            param.thisObject, "mHandleVolumeKeysInWM"
                                        )
                                    ) {
                                        // When {@link #mHandleVolumeKeysInWM} is set, volume key events
                                        // should be dispatched to WM.
                                        if (callMethod(
                                                telecomManager, "isRinging"
                                            ) as Boolean
                                        ) {
                                            // If an incoming call is ringing, either VOLUME key means
                                            // "silence ringer".  We handle these keys here, rather than
                                            // in the InCallScreen, to make sure we'll respond to them
                                            // even if the InCallScreen hasn't come to the foreground yet.
                                            // Look for the DOWN event here, to agree with the "fallback"
                                            // behavior in the InCallScreen.
                                            Log.i(
                                                "PhoneWindowManager",
                                                "interceptKeyBeforeQueueing:" + " VOLUME key-down while ringing: Silence ringer!"
                                            )

                                            // Silence the ringer.  (It's safe to call this
                                            // even if the ringer has already been silenced.)
                                            callMethod(
                                                telecomManager, "silenceRinger"
                                            )

                                            // And *don't* pass this key thru to the current activity
                                            // (which is probably the InCallScreen.)
                                            //ACTION_PASS_TO_USER
                                            result = 0
                                        }
                                    }
                                    var audioMode = AudioManager.MODE_NORMAL
                                    try {
                                        audioMode = callMethod(
                                            callMethod(
                                                param.thisObject, "getAudioService"
                                            ), "getMode"
                                        ) as Int
                                    } catch (e: Exception) {
                                        Log.e(
                                            "PhoneWindowManager",
                                            "Error getting AudioService" + " in interceptKeyBeforeQueueing.",
                                            e
                                        )
                                    }
                                    val isInCall = callMethod(
                                        telecomManager, "isInCall"
                                    ) as Boolean || audioMode == AudioManager.MODE_IN_COMMUNICATION
                                    if (isInCall && result and 1 == 0) {
                                        // If we are in call but we decided not to pass the key to
                                        // the application, just pass it to the session service.
                                        callMethod(
                                            mediaSessionLegacyHelperObject,
                                            "sendVolumeKeyEvent",
                                            event,
                                            AudioManager.USE_DEFAULT_STREAM_TYPE,
                                            false
                                        )
                                    }
                                }
                                if (getBooleanField(
                                        param.thisObject, "mUseTvRouting"
                                    ) || getBooleanField(
                                        param.thisObject, "mHandleVolumeKeysInWM"
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
                                                    if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) KeyEvent.KEYCODE_MEDIA_NEXT
                                                    else KeyEvent.KEYCODE_MEDIA_PREVIOUS

                                                scheduleLongPressKeyEvent(
                                                    event, newKeyCode
                                                )
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
                                            "sendVolumeKeyEvent",
                                            newEvent,
                                            AudioManager.USE_DEFAULT_STREAM_TYPE,
                                            true
                                        )
                                    }
                                }
                            }
                        }
                        if (useHapticFeedback) {
                            callMethod(
                                param.thisObject,
                                "performHapticFeedback",
                                HapticFeedbackConstants.VIRTUAL_KEY,
                                "Virtual Key - Press"
                            )
                        }

                        if (!interactive && isWakeKey && down && callMethod(
                                getObjectField(param.thisObject, "mGlobalKeyManager"),
                                "shouldDispatchFromNonInteractive",
                                keyCode
                            ) as Boolean
                        ) {
                            callMethod(
                                getObjectField(param.thisObject, "mGlobalKeyManager"),
                                "setBeganFromNonInteractive"
                            )
                            result = 1
                            setIntField(
                                param.thisObject, "mPendingWakeKey", -1
                            )
                        }

                        if (isWakeKey) {
                            callMethod(param.thisObject, "wakeUpFromWakeKey", event)
                        }

                        if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            param.result = result
                        }
                    }
                })
        }

        /**
         * Additional functions
         */
        // This gets called when torch tweak broadcast received to set
        // mSupportLongPressPowerWhenNonInteractive back to default value when tweak is disabled. Not really required but cleaner
        fun updateSupportLongPressPowerWhenNonInteractive(value: Boolean) {
            try {
                setBooleanField(
                    PhoneWindowManager, "mSupportLongPressPowerWhenNonInteractive", value
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        private fun isMusicPlayingInActiveSessions(context: Context): Boolean {
            try {
                val mediaSessionManager =
                    context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
                val mediaControllers = mediaSessionManager.getActiveSessions(null)

                for (controller in mediaControllers) {
                    val playbackState = controller.playbackState
                    if (playbackState != null && playbackState.state == android.media.session.PlaybackState.STATE_PLAYING) {
                        return true
                    }
                }
                return false
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
                return false
            }
        }

        private fun scheduleLongPressKeyEvent(origEvent: KeyEvent, keyCode: Int) {
            try {
                val mHandler = getObjectField(PhoneWindowManager, "mHandler") as Handler
                val event = KeyEvent(
                    origEvent.downTime, origEvent.eventTime, origEvent.action, keyCode, 0
                )
                val msg: Message = mHandler.obtainMessage(
                    MSG_DISPATCH_VOLKEY_WITH_WAKE_LOCK, event
                )
                msg.isAsynchronous = true
                mHandler.sendMessageDelayed(
                    msg, ViewConfiguration.getLongPressTimeout().toLong()
                )
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        private fun dispatchMediaKeyWithWakeLockToAudioService(keyEvent: KeyEvent) {
            try {
                val mContext: Context = getObjectField(PhoneWindowManager, "mContext") as Context
                val mediaSessionLegacyHelperObject = callStaticMethod(
                    MediaSessionLegacyHelper, "getHelper", mContext
                )
                if (callMethod(
                        getObjectField(
                            PhoneWindowManager, "mActivityManagerInternal"
                        ), "isSystemReady"
                    ) as Boolean
                ) {
                    callMethod(
                        mediaSessionLegacyHelperObject, "sendMediaButtonEvent", keyEvent, true
                    )
                }
            } catch (e: Exception) {
                val funcName = object {}.javaClass.enclosingMethod?.name ?: "unknown"
                sendLogBroadcast(
                    SystemUIContext, "Function Error", "$funcName - ${e.toString()}"
                )
            }
        }

        private fun isVolumeKey(code: Int): Boolean {
            return code == 25 || code == 24
        }
    }
}