package com.company.dima.musicplayer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerService @Inject constructor() : Service() {
    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return")
    }
}