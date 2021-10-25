package com.company.dima.musicplayer

import android.app.Application

class Application:Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}