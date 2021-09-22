package com.skyyo.compose_template.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
//        Venom.createInstance(this).apply {
//            initialize()
//            start()}
    }

}