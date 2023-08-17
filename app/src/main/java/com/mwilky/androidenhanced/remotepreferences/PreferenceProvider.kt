package com.mwilky.androidenhanced.remotepreferences

import android.content.SharedPreferences
import com.crossbowffs.remotepreferences.RemotePreferenceFile
import com.crossbowffs.remotepreferences.RemotePreferenceProvider
import com.mwilky.androidenhanced.xposed.XposedInit.Companion.FRAMEWORK_PACKAGE
import com.mwilky.androidenhanced.xposed.XposedInit.Companion.FRAMEWORK_PREFS
import com.mwilky.androidenhanced.xposed.XposedInit.Companion.SYSTEMUI_PACKAGE
import com.mwilky.androidenhanced.xposed.XposedInit.Companion.SYSTEMUI_PREFS
import de.robv.android.xposed.XposedBridge


class PreferenceProvider() : RemotePreferenceProvider(
        "com.mwilky.androidenhanced", arrayOf(
            RemotePreferenceFile(SYSTEMUI_PREFS,true),
            RemotePreferenceFile(FRAMEWORK_PREFS, true)
        )
) {

    companion object {

        val systemUIPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
//                if (key == "key_one") {
//                    val newValue = sharedPreferences.getBoolean(key, false)
//                    // Do something with the new value
//                    XposedBridge.log("Preference key_one changed to $newValue")
//                }
            }

        val frameworkPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
//                if (key == "key_one") {
//                    val newValue = sharedPreferences.getBoolean(key, false)
//                    // Do something with the new value
//                    XposedBridge.log("Preference key_one changed to $newValue")
//                }
            }


    }
    @Override
    override fun checkAccess(prefFileName: String, prefKey: String, write: Boolean): Boolean {
        //Get package requesting the prefs
        val callingPackage: String? = callingPackage

        //Only allow read access for module scope
        return if (callingPackage == SYSTEMUI_PACKAGE && prefFileName == SYSTEMUI_PREFS) {
            !write
        } else if (callingPackage == FRAMEWORK_PACKAGE && prefFileName == FRAMEWORK_PREFS)  {
            !write
        } else {
            false
        }
    }
}