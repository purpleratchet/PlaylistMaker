package com.example.playlistmaker.presentation

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPresenter(
    private val button: ImageView,
    private val trackLength: TextView,
    private val previewUrl: String


): MediaContract.Presenter {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                trackLength.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }
    }

    override fun onPlayClicked() {
        button.setImageResource(R.drawable.button_pause)
        when (playerState) {
            STATE_DEFAULT -> {
                playAudio(previewUrl)
            }
            STATE_PREPARED -> {
                resumeAudio()
                button.setImageResource(R.drawable.button_play)
            }
            STATE_PLAYING -> {
                playAudio(previewUrl)
                button.setImageResource(R.drawable.button_pause)
            }
            STATE_PAUSED -> {
                resumeAudio()
                button.setImageResource(R.drawable.button_play)
            }
        }
    }

    init {
        progressRunnable()
        setupMediaPlayer()
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepare()
    }

    override fun onPauseClicked() {
        button.setImageResource(R.drawable.button_play)
        pauseAudio()
    }

    override fun progressRunnable() {
        updateProgress()
        handler.postDelayed(runnable, DELAY)
    }

    private fun updateProgress() {
        trackLength.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
    }
    private fun playAudio(url: String) {
        try {
        mediaPlayer.apply {
            reset()
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                start()
                playerState = STATE_PLAYING
                progressRunnable()
            }
            setOnCompletionListener {
                playerState = STATE_DEFAULT
                trackLength.text = "00:00"
                handler.removeCallbacks(runnable)
            }
        }
            playerState = STATE_PREPARED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun resumeAudio() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        progressRunnable()
    }
    private fun pauseAudio() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }
}