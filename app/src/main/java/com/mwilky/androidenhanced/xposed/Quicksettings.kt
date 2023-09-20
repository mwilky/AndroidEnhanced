package com.mwilky.androidenhanced.xposed

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.view.View
import com.mwilky.androidenhanced.BroadcastUtils
import com.mwilky.androidenhanced.Utils
import com.mwilky.androidenhanced.Utils.Companion.initVibrator
import com.mwilky.androidenhanced.Utils.Companion.mVibrator
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedBridge.hookAllConstructors


class Quicksettings {

    companion object {
        //Hook Classes
        private const val QS_TILE_IMPL_CLASS = "com.android.systemui.qs.tileimpl.QSTileImpl"

        //Tweak Variables
        var mClickVibrationEnabled: Boolean = false

        fun init(classLoader: ClassLoader?) {

            //Hook Constructors
            val qsTileImpl = findClass(QS_TILE_IMPL_CLASS, classLoader)

            hookAllConstructors(qsTileImpl, QSTileImplConstructorHook)

            //QS tile click vibration
            findAndHookMethod(
                QS_TILE_IMPL_CLASS, classLoader, "click",
                View::class.java,
                clickHook
            )

            //QS tile click vibration
            findAndHookMethod(
                QS_TILE_IMPL_CLASS,
                classLoader,
                "longClick",
                View::class.java,
                longClickHook
            )

        }

        // Hooked functions
        // register the receiver
        private val QSTileImplConstructorHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {

                val mContext = getObjectField(param.thisObject, "mContext")
                        as Context

                BroadcastUtils.registerBroadcastReceiver(
                    mContext, Utils.qsTileVibration,
                    param.thisObject.toString()
                )

            }
        }

        //vibrate on short press
        private val clickHook: XC_MethodHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (mClickVibrationEnabled) {
                    val mContext = getObjectField(param.thisObject, "mContext")
                            as Context
                    initVibrator(mContext)
                    val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                    mVibrator.vibrate(vibrationEffect)
                }
            }
        }

        //vibrate on long press
        private val longClickHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (mClickVibrationEnabled) {
                    val mContext = getObjectField(param.thisObject, "mContext")
                            as Context
                    initVibrator(mContext)
                    val vibrationEffect = VibrationEffect.createPredefined(EFFECT_CLICK)
                    mVibrator.vibrate(vibrationEffect)
                }
            }
        }


        //Additional functions
    }
}