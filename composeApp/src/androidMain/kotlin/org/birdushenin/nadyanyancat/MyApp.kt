package org.birdushenin.nadyanyancat

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
