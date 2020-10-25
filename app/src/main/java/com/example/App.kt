package com.example

import android.app.Application
import com.example.graduation.BuildConfig
import com.facebook.drawee.backends.pipeline.Fresco
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Fresco.initialize(this)
    }
}