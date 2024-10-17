package com.mwilky.androidenhanced.xposed

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
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
import com.mwilky.androidenhanced.HookedClasses.Companion.CENTRAL_SURFACES_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.CLOCK_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.COLLAPSED_STATUSBAR_FRAGMENT_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.HEADS_UP_APPEARANCE_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.STATUSBAR_ICON_CONTROLLER_IMPL_CLASS
import com.mwilky.androidenhanced.HookedClasses.Companion.SYSTEM_UI_APPLICATION_CLASS
import com.mwilky.androidenhanced.xposed.SystemUIApplication.Companion.getApplicationContext
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getAdditionalStaticField
import de.robv.android.xposed.XposedHelpers.getBooleanField
import de.robv.android.xposed.XposedHelpers.getFloatField
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getSurroundingThis
import de.robv.android.xposed.XposedHelpers.newInstance
import de.robv.android.xposed.XposedHelpers.setBooleanField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.roundToInt


class Statusbar {

    companion object {


        // Tweak Variables
        var mDoubleTapToSleepEnabled: Boolean = false
        var mStatusbarBrightnessControlEnabled: Boolean = false
        var mStatusbarClockPosition: Int = 0
        var mStatusbarClockSecondsEnabled: Boolean = false
        var mHideCollapsedAlarmEnabled: Boolean = true
        var mHideCollapsedVolumeEnabled: Boolean = true
        var mHideCollapsedCallStrengthEnabled: Boolean = true

        // Class Objects
        lateinit var notificationPanelViewController: Any
        lateinit var notificationPanelView: Any
        lateinit var phoneStatusBarViewController: Any
        lateinit var phoneStatusBarView: Any
        lateinit var centralSurfacesImpl: Any
        lateinit var displayManager: DisplayManager
        lateinit var shadeController: Any
        lateinit var collapsedStatusBarFragment: Any
        lateinit var clock: Any

        // Class references
        private var brightnessUtilsClass: Class<*>? = null
        private var viewClippingUtil: Class<*>? = null
        private var `headsUpAppearanceController$$ExternalSyntheticLambda0`: Class<*>? = null
        private var `headsUpAppearanceController$$ExternalSyntheticLambda4`: Class<*>? = null
        private var `carrierTextManager$$ExternalSyntheticLambda1`: Class<*>? = null


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

            brightnessUtilsClass =
                findClass("com.android.settingslib.display.BrightnessUtils", classLoader)

            viewClippingUtil =
                findClass("com.android.internal.widget.ViewClippingUtil", classLoader)

            `headsUpAppearanceController$$ExternalSyntheticLambda0` = findClass(
                "com.android.systemui.statusbar.phone." + "HeadsUpAppearanceController$\$ExternalSyntheticLambda0",
                classLoader
            )

            `headsUpAppearanceController$$ExternalSyntheticLambda4` = findClass(
                "com.android.systemui.statusbar.phone." + "HeadsUpAppearanceController$\$ExternalSyntheticLambda4",
                classLoader
            )

            val notificationPanelViewControllerClass =
                findClass(NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS, classLoader)

            val phoneStatusBarViewControllerClass =
                findClass(PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS, classLoader)

            // Constructor hooks
            hookAllConstructors(notificationPanelViewControllerClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val view = getObjectField(param.thisObject, "mView") as View

                    // Set objects so we can use them in additional functions
                    notificationPanelViewController = param.thisObject
                    notificationPanelView = view

                    val context = view.context as Context

                    doubleTapGesture = GestureDetector(context, object : SimpleOnGestureListener() {
                        override fun onDoubleTap(event: MotionEvent): Boolean {
                            val powerManager =
                                context.getSystemService(Context.POWER_SERVICE) as PowerManager
                            callMethod(powerManager, "goToSleep", event.eventTime)
                            return true
                        }
                    })
                }
            })

            hookAllConstructors(phoneStatusBarViewControllerClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val view = getObjectField(param.thisObject, "mView") as View

                    // Set objects so we can use them in additional functions
                    phoneStatusBarViewController = param.thisObject
                    phoneStatusBarView = view
                }
            })

            // Hooked methods
            // Double tap to sleep
            findAndHookMethod(NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS,
                classLoader,
                "onTouch",
                View::class.java,
                MotionEvent::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val event = param.args[1] as MotionEvent
                        val mCommandQueue = getObjectField(
                            getSurroundingThis(param.thisObject), "mCommandQueue"
                        )
                        val commandQueuePanelsEnabled =
                            callMethod(mCommandQueue, "panelsEnabled") as Boolean

                        if (mStatusbarBrightnessControlEnabled) {
                            brightnessControl(event)
                            if (!commandQueuePanelsEnabled) param.result = null
                        }
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                        val event = param.args[1] as MotionEvent

                        val upOrCancel =
                            event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL

                        onBrightnessChanged(upOrCancel)

                        if (mDoubleTapToSleepEnabled) {
                            doubleTapGesture.onTouchEvent(event)
                        }
                    }
                })

            // Statusbar brightness control
            findAndHookMethod(PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS,
                classLoader,
                "onTouch",
                MotionEvent::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val event = param.args[0] as MotionEvent
                        val centralSurfaces = getObjectField(param.thisObject, "centralSurfaces")
                        val mCommandQueue = getObjectField(centralSurfaces, "mCommandQueue")
                        val commandQueuePanelsEnabled =
                            callMethod(mCommandQueue, "panelsEnabled") as Boolean

                        if (mStatusbarBrightnessControlEnabled) {
                            brightnessControl(event)
                            if (!commandQueuePanelsEnabled) param.result = null
                        }
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                        val event = param.args[0] as MotionEvent
                        val upOrCancel =
                            event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL

                        onBrightnessChanged(upOrCancel)

                        if (mDoubleTapToSleepEnabled) {
                            doubleTapGesture.onTouchEvent(event)
                        }

                    }
                })

            // Statusbar brightness control
            findAndHookMethod(CENTRAL_SURFACES_IMPL_CLASS,
                classLoader,
                "start",
                object : XC_MethodHook() {
                    @SuppressLint("DiscouragedApi")
                    override fun afterHookedMethod(param: MethodHookParam) {
                        centralSurfacesImpl = param.thisObject

                        val context = getObjectField(param.thisObject, "mContext") as Context

                        displayManager =
                            context.getSystemService(DisplayManager::class.java) as DisplayManager

                        shadeController = getObjectField(param.thisObject, "mShadeController")

                        val powerManager =
                            getObjectField(param.thisObject, "mPowerManager") as PowerManager

                        // Set statusbar brightness control variables
                        displayId = getIntField(param.thisObject, "mDisplayId")
                        minimumBacklight =
                            callMethod(powerManager, "getBrightnessConstraint", 0) as Float
                        maximumBacklight =
                            callMethod(powerManager, "getBrightnessConstraint", 1) as Float
                        quickQsOffsetHeight = context.resources.getDimensionPixelSize(
                            context.resources.getIdentifier(
                                "quick_qs_offset_height", "dimen", "android"
                            )
                        )
                    }
                })

            //Clock position
            findAndHookMethod(COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "onViewCreated",
                View::class.java,
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        collapsedStatusBarFragment = param.thisObject

                    }

                    override fun afterHookedMethod(param: MethodHookParam) {

                        val mClockView = getObjectField(param.thisObject, "mClockView") as View

                        mDefaultClockContainer = mClockView.parent

                        setStatusbarClockPosition()
                    }
                })

            //Clock position
            findAndHookMethod(COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "animateHiddenState",
                if (Build.VERSION.SDK_INT >= 35) Int::class.javaPrimitiveType else View::class.java,
                if (Build.VERSION.SDK_INT >= 35) View::class.java else Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val view: View = (if (Build.VERSION.SDK_INT >= 35) param.args[1] else param.args[0]  as View) as View

                        val mClockView: View =
                            getObjectField(collapsedStatusBarFragment, "mClockView") as View

                        if (view == mClockView && mStatusbarClockPosition != 0) {
                            param.result = null
                        }

                    }
                })

            //Clock position
            findAndHookMethod(HEADS_UP_APPEARANCE_CONTROLLER_CLASS,
                classLoader,
                "setShown",
                Boolean::class.javaPrimitiveType,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        val mShown = getBooleanField(param.thisObject, "mShown")

                        val isShown = param.args[0] as Boolean
                        val mView = getObjectField(param.thisObject, "mView") as View

                        val mContext = mView.context

                        val mClockView = getObjectField(param.thisObject, "mClockView") as View
                        val mParentClippingParams =
                            getObjectField(param.thisObject, "mParentClippingParams")

                        val mOperatorNameViewOptional =
                            getObjectField(param.thisObject, "mOperatorNameViewOptional")

                        val mStatusBarStateController =
                            getObjectField(param.thisObject, "mStatusBarStateController")

                        val mCommandQueue = getObjectField(param.thisObject, "mCommandQueue")

                        if (mShown != isShown) {
                            setBooleanField(param.thisObject, "mShown", isShown)
                            if (isShown) {
                                callStaticMethod(
                                    viewClippingUtil,
                                    "setClippingDeactivated",
                                    mView,
                                    true,
                                    mParentClippingParams
                                )

                                if (mView.javaClass.name == "com.android.systemui.statusbar.HeadsUpStatusBarView") {
                                    mView.visibility = View.VISIBLE
                                }

                                callMethod(param.thisObject, "show", mView)

                                if (mStatusbarClockPosition == 0) {
                                    callMethod(param.thisObject, "hide", mClockView, 4, null)
                                }

                                callMethod(
                                    mOperatorNameViewOptional, "ifPresent", newInstance(
                                        `headsUpAppearanceController$$ExternalSyntheticLambda0`,
                                        param.thisObject,
                                        1
                                    )
                                )

                            } else {
                                callMethod(param.thisObject, "show", mClockView)

                                callMethod(
                                    mOperatorNameViewOptional, "ifPresent", newInstance(
                                        `headsUpAppearanceController$$ExternalSyntheticLambda0`,
                                        param.thisObject,
                                        2
                                    )
                                )
                                callMethod(
                                    param.thisObject, "hide", mView, 8, newInstance(
                                        `headsUpAppearanceController$$ExternalSyntheticLambda4`,
                                        param.thisObject,
                                        0
                                    )
                                )
                            }

                            if (callMethod(mStatusBarStateController, "getState") as Int != 0) {
                                callMethod(
                                    mCommandQueue,
                                    "recomputeDisableFlags",
                                    callMethod(mContext, "getDisplayId"),
                                    false
                                )
                            }
                        }
                        return null
                    }
                })

            //Clock seconds
            findAndHookMethod(CLOCK_CLASS,
                classLoader,
                "updateShowSeconds",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        setBooleanField(
                            param.thisObject, "mShowSeconds", mStatusbarClockSecondsEnabled
                        )
                    }
                })

            //Clock seconds
            findAndHookMethod(CLOCK_CLASS,
                classLoader,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        clock = param.thisObject as View
                    }
                })

            findAndHookMethod(COLLAPSED_STATUSBAR_FRAGMENT_CLASS,
                classLoader,
                "updateBlockedIcons",
                object : XC_MethodReplacement() {
                    @SuppressLint("DiscouragedApi")
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {

                        val mContext = (collapsedStatusBarFragment as Fragment).context as Context

                        val mMainExecutor = getObjectField(param.thisObject, "mMainExecutor") as Executor

                        val mBlockedIcons =
                            getObjectField(param.thisObject, "mBlockedIcons") as ArrayList<String>

                        mBlockedIcons.clear()

                        val blockListArray = mContext.resources.getStringArray(
                            mContext.resources.getIdentifier(
                                "config_collapsed_statusbar_icon_blocklist",
                                "array",
                                "com.android.systemui"
                            )
                        )

                        val blockList = blockListArray.toList()

                        val vibrateIconSlot = mContext.resources.getString(
                            mContext.resources.getIdentifier(
                                "status_bar_volume", "string", "android"
                            )
                        )

                        val showVibrateIcon = Settings.Secure.getInt(
                            mContext.contentResolver, "status_bar_show_vibrate_icon", 0
                        ) == 0

                        for (i in blockList.indices) {
                            when (blockList[i]) {
                                vibrateIconSlot -> {
                                    if (showVibrateIcon && mHideCollapsedVolumeEnabled) {
                                        mBlockedIcons.add(blockList[i])
                                    }
                                }

                                "alarm_clock" -> {
                                    if (mHideCollapsedAlarmEnabled) {
                                        mBlockedIcons.add(blockList[i])
                                    }
                                }

                                "call_strength" -> {
                                    if (mHideCollapsedAlarmEnabled) {
                                        mBlockedIcons.add(blockList[i])
                                    }
                                }

                                else -> {
                                    mBlockedIcons.add(blockList[i])
                                }
                            }
                        }

                        mMainExecutor.execute {

                            val mDarkIconManager = getObjectField(param.thisObject, "mDarkIconManager")
                            callMethod(mDarkIconManager, "setBlockList", mBlockedIcons)

                        }

                        return null
                    }

                })
        }

        // Additional functions
        private fun adjustBrightness(x: Int) {
            brightnessChanged = true
            val displayWidth = getFloatField(
                getObjectField(centralSurfacesImpl, "mDisplayMetrics"), "widthPixels"
            )
            val raw = x / displayWidth
            val padded = 0.85f.coerceAtMost(BRIGHTNESS_CONTROL_PADDING.coerceAtLeast(raw))
            val value = (padded - BRIGHTNESS_CONTROL_PADDING) / 0.7f
            val linearFloat = callStaticMethod(
                brightnessUtilsClass,
                "convertGammaToLinearFloat",
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
                getObjectField(centralSurfacesImpl, "mCommandQueue"), "mHandler"
            ) as Handler
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
                phoneStatusBarView, "performHapticFeedback", HapticFeedbackConstants.LONG_PRESS
            )
            adjustBrightness(initialTouchX)
            linger = BRIGHTNESS_CONTROL_LINGER_THRESHOLD + 1
        }

        fun onBrightnessChanged(upOrCancel: Boolean) {
            if (brightnessChanged && upOrCancel) {
                brightnessChanged = false
                val isExpandedVisible = getBooleanField(shadeController, "mExpandedVisible")
                if (justPeeked && isExpandedVisible) {
                    callMethod(
                        notificationPanelViewController, "fling", 10, 1.0f, false, false
                    )
                }
                callMethod(
                    displayManager, "setBrightness", displayId, currentBrightness
                )
            }
        }

        @SuppressLint("DiscouragedApi")
        fun setStatusbarClockPosition() {

            val mContext = getApplicationContext() ?: return

            val sentAllBootPrefs = getAdditionalStaticField(
                findClass(
                    SYSTEM_UI_APPLICATION_CLASS, mContext.classLoader
                ), "mSentAllBootPrefs"
            ) as Boolean

            if (!sentAllBootPrefs) return

            val mStatusBar = getObjectField(collapsedStatusBarFragment, "mStatusBar") as ViewGroup

            //Get the clock containing view
            val mSystemIconArea: LinearLayout = mStatusBar.findViewById(
                mContext.resources.getIdentifier(
                    "statusIcons", "id", "com.android.systemui"
                )
            )
            //Get the clock view
            val mClockView = getObjectField(collapsedStatusBarFragment, "mClockView") as View

            val rightParent = mSystemIconArea.parent as ViewGroup

            //Remove clock
            (mClockView.parent as ViewGroup?)?.removeView(mClockView)

            //Parent view set by module
            val setParent: ViewGroup?

            //Set the paddings of the clock
            val paddingStart: Int = mContext.resources.getDimensionPixelSize(
                mContext.resources.getIdentifier(
                    "status_bar_left_clock_starting_padding", "dimen", "com.android.systemui"
                )
            )
            val paddingEnd: Int = mContext.resources.getDimensionPixelSize(
                mContext.resources.getIdentifier(
                    "status_bar_left_clock_end_padding", "dimen", "com.android.systemui"
                )
            )

            when (mStatusbarClockPosition) {
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
            val context = getObjectField(centralSurfacesImpl, "mContext") as Context
            Settings.System.putFloat(
                context.contentResolver, "screen_brightness_float", value
            )
        }
    }
}