package com.mwilky.androidenhanced

import android.content.Context

/**
 * Keeps exactly one TorchController per process.
 *
 *  • call [ensure] once (e.g. in systemBooted) to create it
 *  • call [toggle] from any other hook
 */
object TorchHolder {
    private var torch: TorchController? = null

    fun ensure(context: Context) {
        if (torch == null) torch = TorchController(context)
    }

    fun toggle() = torch?.toggle()

    val isOn: Boolean
        get() = torch?.enabled?.value == true

}

