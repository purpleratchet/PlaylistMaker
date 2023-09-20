package com.example.playlistmaker.presentation

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.domain.PlayerInteractor
import com.example.playlistmaker.presentation.ui.MediaContract

class MediaPresenter(
    private val btnPlay: ImageView,
    private val btnPause: ImageView,
    private val progressTime: TextView,
    private val previewUrl: String?,
    private val playerInteractor: PlayerInteractor,
) : MediaContract.Presenter {
    private val progressHandler = Handler(Looper.getMainLooper())
    private lateinit var progressRunnable: Runnable

    init {
        progressRunnable = Runnable {
            updateProgressTime()
            progressHandler.postDelayed(progressRunnable, 300)
        }

        playerInteractor.preparePlayer(previewUrl ?: "", {}, {
            progressTime.text = "00:00"
            progressHandler.removeCallbacks(progressRunnable)
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE
        })
    }

    private fun updateProgressTime() {
        val progress = playerInteractor.currentPosition()
        progressTime.text = formatTime(progress)
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = timeInMillis / 1000 / 60
        val seconds = timeInMillis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPlayClicked() {
        btnPlay.visibility = View.INVISIBLE
        btnPause.visibility = View.VISIBLE
        playerInteractor.startAudio()
        progressHandler.post(progressRunnable)
    }

    override fun onPauseAudioClicked() {
        btnPause.visibility = View.GONE
        btnPlay.visibility = View.VISIBLE
        playerInteractor.pauseAudio()
        progressHandler.removeCallbacks(progressRunnable)
    }

    override fun onPause() {
        btnPause.visibility = View.GONE
        btnPlay.visibility = View.VISIBLE
        playerInteractor.pauseAudio()
    }
}