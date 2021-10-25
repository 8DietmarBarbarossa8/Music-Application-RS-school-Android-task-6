package com.company.dima.musicplayer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.company.dima.musicplayer.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isSeekBarTrackingTouch = false

    @Inject
    lateinit var model: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as Application).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // enable scrolling for textLog
        binding.textLog.movementMethod = ScrollingMovementMethod()

        setListenersPlayer()
        setListenerSeekBar()
        registerObservers()
        prefatorySetSeekBarSettings()
    }

    private fun registerObservers() {
        model.currentTrackLiveData.observe(this,
            {
                toLog("Change track: $it")
                showInfo()
            })

        model.isPlayingChangedLiveData.observe(this,
            {
                toLog("Change isPlaying state: $it")
                showButtonPlayPause(it)
                setSeekBarDuration(model.getDuration())
            })

        model.progressChangedLiveData.observe(this,
            {
                //toLog("Change Progress state: $it")
                if (!isSeekBarTrackingTouch) {
                    showProgressBar(it)
                }
            })
    }

    private fun setListenersPlayer() {
        binding.playerNext.setOnClickListener {
            toLog("Click Next button")
            if (model.nextTrack()) {
                prefatorySetSeekBarSettings()
            }

        }
        binding.playerPrevious.setOnClickListener {
            toLog("Click Previous button")
            if (model.previousTrack()) {
                prefatorySetSeekBarSettings()
            }
        }
        binding.playerPlay.setOnClickListener {
            toLog("Click Play button")
            model.playPlayer()
        }
        binding.playerPause.setOnClickListener {
            toLog("Click Pause button")
            model.pausePlayer()
        }
        binding.playerStop.setOnClickListener {
            toLog("Click Stop button")
            model.stopPlayer()
        }
    }

    private fun setListenerSeekBar() {
        binding.playSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                playSeekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                setSeekBarPosition(progress.toLong())
            }

            override fun onStartTrackingTouch(playSeekBar: SeekBar?) {
                isSeekBarTrackingTouch = true
            }

            override fun onStopTrackingTouch(playSeekBar: SeekBar?) {
                playSeekBar?.let {
                    model.seekToPositionPlayer(it.progress.toLong())
                    isSeekBarTrackingTouch = false
                }
            }
        })
    }

    private fun showButtonPlayPause(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playerPlay.visibility = View.GONE
            binding.playerPause.visibility = View.VISIBLE
        } else {
            binding.playerPlay.visibility = View.VISIBLE
            binding.playerPause.visibility = View.GONE
        }
    }

    private fun prefatorySetSeekBarSettings() {
        setSeekBarPosition(0)
        model.currentTrack?.let {
            setSeekBarDuration(it.duration)
        } ?: setSeekBarDuration(0)
    }

    private fun setSeekBarPosition(position: Long) {
        binding.playPosition.text = mscToTime(position)
    }

    private fun setSeekBarDuration(duration: Long) {
        var applyDuration = 0L
        if (duration > 0) {
            applyDuration = duration
        }
        binding.playSeekBar.max = applyDuration.toInt()

        //Log.d(TAG, "setSeekBarDuration - $applyDuration")
        binding.playDuration.text = mscToTime(applyDuration)
    }

    private fun showInfo() {
        model.currentTrack?.let { track ->
            binding.trackTitle.text = track.title
            showCover(track.bitmapUri)
        }
    }

    private fun showProgressBar(progress: Long) {
        val progressView = progress.toInt()
        if (progressView != binding.playSeekBar.progress) {
            binding.playSeekBar.progress = progressView
        }
    }

    private fun toLog(event: String) {
        binding.textLog.append("$event\n")
    }

    private fun showCover(uri: String) {
        val view = binding.trackCover
        Glide.with(view.context)
            .load(uri)
            .centerCrop()
            //.error(R.drawable.ic_baseline_close_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    toLog("Image loading - Error")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    toLog("Image loading - Ok")
                    return false
                }
            })
            .into(view)
    }
}