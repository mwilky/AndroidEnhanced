package com.mwilky.androidenhanced

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.util.Log
import com.mwilky.androidenhanced.MainActivity.Companion.TAG

class Utils(context: Context, handler: Handler) {

    companion object {
        //Torch enabled
        var isTorchEnabled: Boolean = false
        //App Settings
        const val ISDEVICESUPPORTEDKEY = "isDeviceSupported"
        const val ISONBOARDINGCOMPLETEDKEY = "isOnboardingComplete"
        //Tweak Values
        const val torchPowerScreenOff = "bool_LongPressPowerTorchScreenOff"
        const val torchAutoOffScreenOn = "bool_TorchAutoOffScreenOn"
    }

    private val mContext: Context = context
    private val mHandler: Handler = handler
    private val mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE)
                as CameraManager
    private var mTorchCallback: TorchCallback = TorchCallback()

    fun registerTorchCallback() {
        mCameraManager.registerTorchCallback(mTorchCallback, mHandler)
    }

    fun toggleTorchMode() {
        setTorchMode(!isTorchEnabled)
    }

    private fun getCameraWithFlash(): String? {
        for (cameraId in mCameraManager.cameraIdList) {
            try {
                val characteristics = mCameraManager.getCameraCharacteristics(cameraId)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash == true && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId
                }
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Couldn't get camera characteristics.", e)
            }
        }
        return null
    }

    private fun setTorchMode(enabled: Boolean): Boolean {
        if (isTorchEnabled != enabled) {
            isTorchEnabled = enabled
            try {
                val cameraId = getCameraWithFlash() ?: return false
                mCameraManager.setTorchMode(cameraId, enabled)
                return true
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Couldn't set torch mode.", e)
            }
        }
        return false
    }

    inner class TorchCallback : CameraManager.TorchCallback() {

        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (isTorchEnabled != enabled) {
                isTorchEnabled = enabled
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            isTorchEnabled = false
        }
    }
}