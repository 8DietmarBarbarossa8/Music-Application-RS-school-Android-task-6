package com.company.dima.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.IOException

class MediaService : Service(), OnCompletionListener,
    OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
    OnInfoListener,
    OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            mediaFile = intent?.getExtras()?.getString("media")
            Log.d("Service", "PRILEteLO" + mediaFile!!)
        } catch (e: NullPointerException) {
            stopSelf();
        }
        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaFile != null && mediaFile != "")
            initMediaPlayer();


        return super.onStartCommand(intent, flags, startId)
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var mediaFile: String? = null
    private var resumePosition = 0
    private var audioManager: AudioManager? = null

    // Binder given to clients
    private val iBinder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }


    fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnPreparedListener(this)

        mediaPlayer.setOnBufferingUpdateListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
        mediaPlayer.setOnInfoListener(this)
        mediaPlayer.reset()

        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(mediaFile)
                prepareAsync()

            } catch (e: IOException) {
                e.printStackTrace()
                stopSelf()
            }
        }

    }


    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    override fun onCompletion(mp: MediaPlayer) {
        //Invoked when playback of a media source has completed.
        stopMedia()
        stopSelf()
    }

    //Handle errors
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked when there has been an error during an asynchronous operation.

        when (what) {
            MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra"
            )
            MEDIA_ERROR_SERVER_DIED -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR SERVER DIED $extra"
            )
            MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }

        return false
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked to communicate some info.
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        //Invoked when the media source is ready for playback.
        playMedia()
    }

    override fun onSeekComplete(mp: MediaPlayer) {
        //Invoked indicating the completion of a seek operation.
    }

    override fun onAudioFocusChange(focusState: Int) {
        //Invoked when the audio focus of the system is updated.
        //Invoked when the audio focus of the system is updated.
        when (focusState) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // resume playback
                if (mediaPlayer == null) initMediaPlayer() else if (!mediaPlayer.isPlaying) mediaPlayer.start()
                mediaPlayer.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
                //mediaPlayer=null // null must be
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->             // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying) mediaPlayer.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->             // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying) mediaPlayer.setVolume(0.1f, 0.1f)
        }
    }

    inner class LocalBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    fun playMedia() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
    }

    fun pauseMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            resumePosition = mediaPlayer.currentPosition
        }
    }

    fun resumeMedia() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(resumePosition)
            mediaPlayer.start()
        }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            true
        } else false
        //Could not gain focus
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager!!.abandonAudioFocus(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }
}