package com.mwilky.androidenhanced

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SETTINGS_KEY")

class DataStoreManager(context: Context) {

    val dataStore = context.dataStore

    // Keys
    companion object {
        val onboardingCompletedKey = booleanPreferencesKey("ONBOARDING_COMPLETED")
        val supportedDeviceKey = booleanPreferencesKey("SUPPORTED_DEVICE")
    }

    // Function to save whether the device is supported
    suspend fun saveIsDeviceSupported(isSupported: Boolean) {
        dataStore.edit { preferences ->
            preferences[supportedDeviceKey] = isSupported
        }
    }

    // Function to retrieve the value of supportedDeviceKey as a Flow<Boolean>
    val isDeviceSupportedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[supportedDeviceKey] ?: false
    }
}
