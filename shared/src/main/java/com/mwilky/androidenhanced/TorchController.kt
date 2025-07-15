package com.mwilky.androidenhanced

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable
import java.util.concurrent.Executor

/**
 * Flashlight helper for API 33+ only.
 *
 * Usage:
 *   val torch = TorchController(context) ?: error("No rear flash")
 *   torch.toggle()
 *   torch.on()
 *   torch.off()
 *   torch.level(5)         // 1-10, variable brightness
 */
class TorchController private constructor(
    private val mgr: CameraManager,
    private val camId: String,
    private val exec: Executor
) : Closeable {

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled

    private var ready = false
    private var pending: (() -> Unit)? = null

    private val cb = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(id: String, on: Boolean) {
            if (id == camId) {
                _enabled.value = on
                ready = true
                pending?.invoke()
                pending = null
            }
        }

        override fun onTorchModeUnavailable(id: String) {
            if (id == camId) _enabled.value = false
        }
    }

    init {
        mgr.registerTorchCallback(exec, cb)
    }

    fun toggle() {
        if (ready) set(!_enabled.value)
        else pending = { set(!_enabled.value) }
    }

    fun on() = set(true)
    fun off() = set(false)
    fun level(lvl: Int) = if (lvl <= 0) off() else mgr.turnOnTorchWithStrengthLevel(camId, lvl)
    private fun set(on: Boolean) = mgr.setTorchMode(camId, on)
    override fun close() = mgr.unregisterTorchCallback(cb)

    companion object {
        operator fun invoke(ctx: Context): TorchController? {
            val app = ctx.applicationContext
            val mgr = app.getSystemService(CameraManager::class.java)
            val id = mgr.cameraIdList.firstOrNull { c ->
                val ch = mgr.getCameraCharacteristics(c)
                ch[CameraCharacteristics.FLASH_INFO_AVAILABLE] == true &&
                        ch[CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_BACK
            } ?: return null
            val exec = ContextCompat.getMainExecutor(app)
            return TorchController(mgr, id, exec)
        }
    }
}

