package com.ramayan.mycom.player.sampleapp

import android.app.Application
import com.ramayan.mycom.BuildConfig


import timber.log.Timber

/**
 * Created by hugo
 * on 14/09/2017.
 */

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
