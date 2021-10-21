package com.company.dima.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var player: MediaService? = null
    var servicesBound = false

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaService.LocalBinder
            player = binder.service
            servicesBound = true
            Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            servicesBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("ServiceState", servicesBound)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("ServiceState", servicesBound);
        super.onSaveInstanceState(outState)
    }

    private fun playAudio(media: String) {
        //Check is service is active
        if (!servicesBound) {
            val playerIntent = Intent(this, MediaService::class.java)
            playerIntent.putExtra("media", media)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            val broadcastIntent = Intent(media)
            sendBroadcast(broadcastIntent)
        }
    }
}