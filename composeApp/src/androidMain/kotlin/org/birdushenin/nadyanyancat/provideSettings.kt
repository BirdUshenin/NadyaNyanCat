package org.birdushenin.nadyanyancat

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

lateinit var appContext: Context

actual fun provideSettings(): Settings {

    return SharedPreferencesSettings(
        appContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    )
}