package com.company.dima.musicplayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.company.dima.musicplayer.ProgressTracker.PositionListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(private val context: Context) {
    private var exoPlayer: SimpleExoPlayer? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var playWhenReady = true
    private var playingUri: String? = null
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var onIsPlayingChangedCallback: (() -> Unit)? = null
    private var tracker: ProgressTracker? = null
    private var onProgressChanged: ((position: Long) -> Unit)? = null
    val duration :Long get() = exoPlayer?.duration ?: 0
    val isPlaying: Boolean get() = (exoPlayer?.isPlaying) ?: false

    private fun initializePlayer() {
        if (exoPlayer != null) return

        exoPlayer = SimpleExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }

        exoPlayer?.let { player ->
            tracker = ProgressTracker(player, object : PositionListener {
                override fun progress(position: Long) {
                    onProgressChanged?.invoke(position)
                }
            })
        }


    }

    fun play(uri: String) {
        if (exoPlayer == null) {
            initializePlayer()
        }

        if (playingUri == uri) {
            if (exoPlayer?.playWhenReady == false) {
                exoPlayer?.playWhenReady = true
            }
        } else {
            currentWindow = 0
            playbackPosition = 0
            playingUri = uri
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer?.run {
                setMediaItem(mediaItem)
                playWhenReady = this@MusicPlayer.playWhenReady
                seekTo(currentWindow, playbackPosition)
                prepare()
                play()
            }
        }
    }

    fun pause() {
        exoPlayer?.let {
            it.pause()
            tracker?.pause()
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
        }
    }

    fun stop() {
        exoPlayer?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.removeListener(playbackStateListener)
            it.release()
            tracker?.purgeHandler()
        }
        playingUri = null
        exoPlayer = null
        onIsPlayingChangedCallback?.invoke()
    }

    fun setCallbackPlayingStateChanged(callback: () -> Unit) {
        if (onIsPlayingChangedCallback != callback) {
            onIsPlayingChangedCallback = callback
        }
    }

    fun setCallbackProgressChanged(callback: (position: Long) -> Unit) {
        if (onProgressChanged != callback) {
            onProgressChanged = callback
        }
    }

    fun seekToPosition(position: Long){
        exoPlayer?.seekTo(position)
    }

    private fun playbackStateListener() = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                }
                ExoPlayer.STATE_BUFFERING -> {
                }
                ExoPlayer.STATE_READY -> {
                }
                ExoPlayer.STATE_ENDED -> {
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            onIsPlayingChangedCallback?.invoke()
            if (isPlaying) {
                tracker?.resume()
            }
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            // if loading is canceled, but it not isPlaying then loading - False
            if (!isLoading) {
                onIsPlayingChangedCallback?.invoke()
                if (!isPlaying) {
                    playingUri = null
                }
            }
        }

    }
}

