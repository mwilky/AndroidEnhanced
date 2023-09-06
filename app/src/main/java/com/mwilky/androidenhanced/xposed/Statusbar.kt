package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.os.PowerManager
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookAllConstructors
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField

class Statusbar {

    companion object {
        //Hook Classes
        private const val NotificationPanelViewControllerClass =
            "com.android.systemui.shade.NotificationPanelViewController"
        private const val NotificationPanelViewControllerTouchHandlerClass =
            "com.android.systemui.shade.NotificationPanelViewController\$TouchHandler"
        private const val PhoneStatusBarViewController =
            "com.android.systemui.statusbar.phone.PhoneStatusBarViewController"

        //Tweak Variables
        var mDoubleTapToSleep: Boolean = false

        private lateinit var mDoubleTapGesture: GestureDetector

        fun init(classLoader: ClassLoader?) {

            //HOOK CONSTRUCTOR
            val notificationPanelViewController =
                findClass(NotificationPanelViewControllerClass, classLoader)

            hookAllConstructors(
                notificationPanelViewController,
                constructor_hook
            )

            //Double tap to sleep
            findAndHookMethod(
                NotificationPanelViewControllerTouchHandlerClass,
                classLoader,
                "onTouch",
                View::class.java,
                MotionEvent::class.java,
                onTouch_hook_NotificationPanelViewController
            )

            //Double tap to sleep
            findAndHookMethod(
                PhoneStatusBarViewController,
                classLoader,
                "onTouch",
                MotionEvent::class.java,
                onTouchEvent_hook_PhoneStatusBarViewController,
            )

        }

        //Hooked methods
        //Perform the gesture on shade view
        private var onTouch_hook_NotificationPanelViewController:
                XC_MethodHook? = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val event = param.args[1] as MotionEvent
                if (mDoubleTapToSleep) {
                    mDoubleTapGesture.onTouchEvent(
                        event
                    )
                }
            }
        }

        //Perform the gesture on statusbar view
        private var onTouchEvent_hook_PhoneStatusBarViewController:
                XC_MethodHook? = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val event = param.args[0] as MotionEvent
                if (mDoubleTapToSleep) {
                    mDoubleTapGesture.onTouchEvent(
                        event
                    )
                }
            }
        }

        //Hook constructor to set the gesture Listener
        private val constructor_hook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val mView: View = getObjectField(param.thisObject, "mView")
                    as View

                val mContext: Context = mView.context
                    as Context

                //Register broadcast receiver to receive values
                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.doubleTapToSleep,
                    param.thisObject.toString()
                )

                mDoubleTapGesture =
                    GestureDetector(mContext,
                        object : SimpleOnGestureListener() {
                            override fun onDoubleTap(event: MotionEvent): Boolean {
                                val powerManager =
                                    mContext.getSystemService(Context.POWER_SERVICE)
                                            as PowerManager
                                XposedHelpers.callMethod(
                                    powerManager,
                                    "goToSleep",
                                    event.eventTime
                                )
                                return true
                            }
                        }
                    )
            }
        }
    }
}