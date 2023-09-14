package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getFloatField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class Statusbar {

    companion object {
        // Hook Classes
        private const val NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.shade.NotificationPanelViewController"
        private const val NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS =
            "com.android.systemui.shade.NotificationPanelViewController\$TouchHandler"
        private const val PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.PhoneStatusBarViewController"
        private const val CENTRAL_SURFACES_IMPL_CLASS =
            "com.android.systemui.statusbar.phone.CentralSurfacesImpl"
        private const val COLLAPSED_STATUSBAR_FRAGMENT_CLASS =
            "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment"
        private const val HEADS_UP_APPEARANCE_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.HeadsUpAppearanceController"

        // Tweak Variables
        var doubleTapToSleepEnabled: Boolean = false
        var statusbarBrightnessControlEnabled: Boolean = false
        var statusbarClockPosition: Int = 0

        // Class Objects
        lateinit var notificationPanelViewController: Any
        lateinit var notificationPanelView: Any
        lateinit var phoneStatusBarViewController: Any
        lateinit var phoneStatusBarView: Any
        lateinit var centralSurfacesImpl: Any
        lateinit var displayManager: DisplayManager
        lateinit var shadeController: Any
        lateinit var collapsedStatusBarFragment: Any

        // Class references
        private var rStringClass: Class<*>? = null

        // Statusbar brightness control
        private var minimumBacklight: Float = 0f
        private var maximumBacklight: Float = 0f
        private var quickQsOffsetHeight: Int = 0
        private var displayId: Int = 0
        private var brightnessChanged = false
        private var linger = 0
        private var justPeeked = false
        private var initialTouchX = 0
        private var initialTouchY = 0
        private var currentBrightness = 0f

        //Clock position
        private lateinit var mDefaultClockContainer: Any

        // Constants
        private const val BRIGHTNESS_CONTROL_PADDING = 0.15f
        private const val BRIGHTNESS_CONTROL_LONG_PRESS_TIMEOUT = 750
        private const val BRIGHTNESS_CONTROL_LINGER_THRESHOLD = 20

        private lateinit var doubleTapGesture: GestureDetector

        fun init(classLoader: ClassLoader?) {
            // Hook Constructors
            val notificationPanelViewControllerClass =
                findClass(NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS, classLoader)

            hookAllConstructors(
                notificationPanelViewControllerClass,
                constructorHookNotificationPanelViewController
            )

            val phoneStatusBarViewControllerClass =
                findClass(PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS, classLoader)

            hookAllConstructors(
                phoneStatusBarViewControllerClass,
                constructorHookPhoneStatusBarViewController
            )

            // Double tap to sleep
            findAndHookMethod(
                NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
                classLoader,
                "onTouch",
                View::class.java,
                MotionEvent::class.java,
                onTouchHookNotificationPanelViewController
            )

            // Statusbar brightness control
            findAndHookMethod(
                PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS,
                classLoader,
                "onTouch",
                MotionEvent::class.java,
                onTouchHookPhoneStatusBarViewController
            )

            // Statusbar brightness control
            findAndHookMethod(
                CENTRAL_SURFACES_IMPL_CLASS,
                classLoader,
                "start",
                startHook
            )


            //Clock position
            findAndHookMethod(
                COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "onViewCreated",
                View::class.java,
                Bundle::class.java,
                onViewCreatedHook
            )

            //Clock position
            findAndHookMethod(
                COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "animateHiddenState",
                View::class.java,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                animateHiddenStateHook
            )

            val carrierTextManager =
                findClass(
                    "com.android.keyguard.CarrierTextManager\$\$ExternalSyntheticLambda1",
                    classLoader
                )

            //Clock position
            findAndHookMethod(
                HEADS_UP_APPEARANCE_CONTROLLER_CLASS,
                classLoader,
                "hide",
                View::class.java,
                Int::class.javaPrimitiveType,
                carrierTextManager,
                hideHook
            )

            rStringClass = findClass("com.android.systemui.R\$string", classLoader)
        }

        // Hooked methods
        // Hook constructor to set the gesture behaviour
        private val constructorHookNotificationPanelViewController: XC_MethodHook =
            object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val view = getObjectField(param.thisObject, "mView") as View

                // Set objects so we can use them in additional functions
                notificationPanelViewController = param.thisObject
                notificationPanelView = view

                val context = view.context as Context

                // Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    context, Utils.doubleTapToSleep,
                    param.thisObject.toString()
                )

                doubleTapGesture = GestureDetector(context, object : SimpleOnGestureListener() {
                    override fun onDoubleTap(event: MotionEvent): Boolean {
                        val powerManager = context.getSystemService(Context.POWER_SERVICE)
                                as PowerManager
                        callMethod(powerManager, "goToSleep", event.eventTime)
                        return true
                    }
                })
            }
        }

        // Hook constructor to set objects
        private val constructorHookPhoneStatusBarViewController: XC_MethodHook =
            object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val view = getObjectField(param.thisObject, "mView") as View

                // Set objects so we can use them in additional functions
                phoneStatusBarViewController = param.thisObject
                phoneStatusBarView = view
            }
        }

        //Register the receiver for clock position
        private val onViewCreatedHook: XC_MethodHook =
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    collapsedStatusBarFragment = param.thisObject
                    val mContext = (collapsedStatusBarFragment as Fragment).context
                        as Context

                    // Register broadcast receiver to receive values
                    BroadcastUtils.registerBroadcastReceiver(
                        mContext, Utils.statusBarClockPosition,
                        param.thisObject.toString()
                    )
                }
                override fun afterHookedMethod(param: MethodHookParam) {
                    val mClockView =
                        getObjectField(param.thisObject, "mClockView") as View

                    mDefaultClockContainer = mClockView.parent

                    setStatusbarClockPosition()
                }
            }

        // Perform the gestures on shade view
        private val onTouchHookNotificationPanelViewController: XC_MethodHook =
            object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val event = param.args[1] as MotionEvent
                val centralSurfaces =
                    getObjectField(
                        getSurroundingThis(param.thisObject), "mCentralSurfaces"
                    )
                val commandQueuePanelsEnabled =
                    callMethod(centralSurfaces, "getCommandQueuePanelsEnabled")
                            as Boolean

                if (statusbarBrightnessControlEnabled) {
                    brightnessControl(event)
                    if (!commandQueuePanelsEnabled)
                        param.result = null
                }
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                val event = param.args[1] as MotionEvent

                val upOrCancel =
                    event.action == MotionEvent.ACTION_UP ||
                            event.action == MotionEvent.ACTION_CANCEL

                onBrightnessChanged(upOrCancel)

                if (doubleTapToSleepEnabled) {
                    doubleTapGesture.onTouchEvent(event)
                }
            }
        }

        // Performs the gestures on statusbarview
        private val onTouchHookPhoneStatusBarViewController: XC_MethodHook =
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    val centralSurfaces =
                        getObjectField(param.thisObject, "centralSurfaces")
                    val commandQueuePanelsEnabled =
                        callMethod(centralSurfaces, "getCommandQueuePanelsEnabled")
                            as Boolean

                    if (statusbarBrightnessControlEnabled) {
                        brightnessControl(event)
                        if (!commandQueuePanelsEnabled)
                            param.result = null
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val event = param.args[0] as MotionEvent
                    val upOrCancel =
                        event.action == MotionEvent.ACTION_UP ||
                                event.action == MotionEvent.ACTION_CANCEL

                    onBrightnessChanged(upOrCancel)

                    if (doubleTapToSleepEnabled) {
                        doubleTapGesture.onTouchEvent(event)
                    }

                }
            }

        // Set all variables for statusbar brightness control
        private val startHook: XC_MethodHook = object : XC_MethodHook() {
            @SuppressLint("DiscouragedApi")
            override fun afterHookedMethod(param: MethodHookParam) {
                centralSurfacesImpl = param.thisObject

                val context = getObjectField(param.thisObject, "mContext")
                        as Context

                displayManager = context.getSystemService(DisplayManager::class.java)
                        as DisplayManager

                shadeController = getObjectField(param.thisObject, "mShadeController")

                val powerManager = getObjectField(param.thisObject, "mPowerManager")
                        as PowerManager

                // Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    context, Utils.statusBarBrightnessControl,
                    param.thisObject.toString()
                )

                // Set statusbar brightness control variables
                displayId = getIntField(param.thisObject, "mDisplayId")
                minimumBacklight =
                    callMethod(powerManager, "getBrightnessConstraint", 0)
                            as Float
                maximumBacklight =
                    callMethod(powerManager, "getBrightnessConstraint", 1)
                            as Float
                quickQsOffsetHeight = context.resources.getDimensionPixelSize(
                    context.resources.getIdentifier(
                        "quick_qs_offset_height", "dimen", "android"
                    )
                )
            }
        }

        // Don't hide clock if it is in right position
        private val animateHiddenStateHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val view:View = param.args[0]
                    as View

                val mClockView:View =
                    getObjectField(collapsedStatusBarFragment, "mClockView")
                        as View

                if (view == mClockView && statusbarClockPosition != 0) {
                    param.result = null
                }

            }
        }

        // Don't hide clock if it is in right position
        private val hideHook: XC_MethodHook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val view:View = param.args[0]
                        as View

                val mClockView:View =
                    getObjectField(collapsedStatusBarFragment, "mClockView")
                            as View

                if (view == mClockView && statusbarClockPosition != 0) {
                    param.result = null
                }
            }
        }

        // Additional functions
        private fun adjustBrightness(x: Int) {
            brightnessChanged = true
            val displayWidth =
                getFloatField(
                    getObjectField(centralSurfacesImpl, "mDisplayMetrics"),
                    "widthPixels"
                )
            val raw = x / displayWidth
            val padded = 0.85f.coerceAtMost(BRIGHTNESS_CONTROL_PADDING.coerceAtLeast(raw))
            val value = (padded - BRIGHTNESS_CONTROL_PADDING) / 0.7f
            val linearFloat = XposedHelpers.callStaticMethod(
                rStringClass, "convertGammaToLinearFloat",
                (65535.0f * value).roundToInt(),
                minimumBacklight,
                maximumBacklight
            ) as Float
            currentBrightness = linearFloat
            callMethod(displayManager, "setTemporaryBrightness", displayId, linearFloat)
            CoroutineScope(Dispatchers.IO).launch {
                val brightnessControl = BrightnessControl(linearFloat)
                brightnessControl.adjustBrightness()
            }
        }

        fun brightnessControl(event: MotionEvent) {
            val mHandler = getObjectField(
                getObjectField(centralSurfacesImpl, "mCommandQueue"), "mHandler")
                    as Handler
            val context = getObjectField(centralSurfacesImpl, "mContext") as Context
            val action = event.action
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            if (action == 0) {
                if (y < quickQsOffsetHeight) {
                    linger = 0
                    initialTouchX = x
                    initialTouchY = y
                    justPeeked = true
                    mHandler.removeCallbacks(longPressBrightnessChange)
                    mHandler.postDelayed(
                        longPressBrightnessChange, BRIGHTNESS_CONTROL_LONG_PRESS_TIMEOUT.toLong()
                    )
                }
            } else if (action == 2) {
                val i = quickQsOffsetHeight
                if (y < i && justPeeked) {
                    if (linger > 20) {
                        adjustBrightness(x)
                        return
                    }
                    val xDiff = abs(x - initialTouchX)
                    val yDiff = abs(y - initialTouchY)
                    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
                    if (xDiff > yDiff) {
                        linger++
                    }
                    if (xDiff > touchSlop || yDiff > touchSlop) {
                        mHandler.removeCallbacks(longPressBrightnessChange)
                        return
                    }
                    return
                }
                if (y > i) {
                    justPeeked = false
                }
                mHandler.removeCallbacks(longPressBrightnessChange)
            } else if (action == 1 || action == 3) {
                mHandler.removeCallbacks(longPressBrightnessChange)
            }
        }

        private val longPressBrightnessChange: Runnable = Runnable {
            callMethod(
                phoneStatusBarView,
                "performHapticFeedback",
                HapticFeedbackConstants.LONG_PRESS
            )
            adjustBrightness(initialTouchX)
            linger = BRIGHTNESS_CONTROL_LINGER_THRESHOLD + 1
        }

        fun onBrightnessChanged(upOrCancel: Boolean) {
            if (brightnessChanged && upOrCancel) {
                brightnessChanged = false
                val isExpandedVisible =
                    getBooleanField(shadeController, "isExpandedVisible")
                if (justPeeked && isExpandedVisible) {
                    callMethod(
                        notificationPanelViewController,
                        "fling",
                        10,
                        false,
                        false
                    )
                }
                callMethod(
                    displayManager, "setBrightness", displayId, currentBrightness
                )
            }
        }

        @SuppressLint("DiscouragedApi")
        fun setStatusbarClockPosition() {
            val mContext = (collapsedStatusBarFragment as Fragment).context
                    as Context

            val mStatusBar = getObjectField(collapsedStatusBarFragment, "mStatusBar")
                    as ViewGroup

            //Get the clock containing view
            val mSystemIconArea: LinearLayout =
                mStatusBar.findViewById(
                    mContext.resources.getIdentifier(
                        "statusIcons",
                        "id",
                        "com.android.systemui"
                    )
                )
            //Get the clock view
            val mClockView = getObjectField(collapsedStatusBarFragment, "mClockView")
                    as View

            val rightParent = mSystemIconArea.parent as ViewGroup

            //Remove clock
            (mClockView.parent as ViewGroup?)?.removeView(mClockView)

            //Parent view set by module
            var setParent: ViewGroup? = null

            //Set the paddings of the clock
            val paddingStart: Int = mContext.resources.getDimensionPixelSize(
                mContext.resources.getIdentifier(
                    "status_bar_left_clock_starting_padding",
                    "dimen",
                    "com.android.systemui"
                )
            )
            val paddingEnd: Int = mContext.resources.getDimensionPixelSize(
                mContext.resources.getIdentifier(
                    "status_bar_left_clock_end_padding",
                    "dimen",
                    "com.android.systemui"
                )
            )

            when (statusbarClockPosition) {
                //left side
                0 -> {
                    setParent = mDefaultClockContainer as ViewGroup
                    setParent.addView(mClockView, 0)
                    mClockView.setPadding(paddingStart, 0, paddingEnd, 0)
                }
                //right side
                1 -> {
                    setParent = rightParent
                    setParent.addView(mClockView)
                    mClockView.setPadding(paddingEnd * 2, 0, 0, 0)
                }

            }
        }

    }

    class BrightnessControl(private val value: Float) {
        fun adjustBrightness() {
            val context = getObjectField(centralSurfacesImpl, "mContext")
                    as Context
            Settings.System.putFloat(
                context.contentResolver, "screen_brightness_float",
                value
            )
        }
    }
}
