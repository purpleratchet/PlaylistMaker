package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackUrl: String
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<PlayerState>()
    private val timerLiveData = MutableLiveData<String>()
    private val clickAllowedLiveData = MutableLiveData<Boolean>()
    fun observeState(): LiveData<PlayerState> = stateLiveData
    fun observeTimer(): LiveData<String> = timerLiveData
    fun observeClickAllowed(): LiveData<Boolean> = clickAllowedLiveData

    init {
        renderState(PlayerState.Default)
        preparePlayer()
        setOnCompleteListener()
        isClickAllowed()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(trackUrl) {
            renderState(PlayerState.Prepared)
        }
    }

    private fun startAudioPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerState.Playing(playerInteractor.getCurrentPosition()))
    }

    private fun pauseAudioPlayer() {
        playerInteractor.pausePlayer()
        renderState(PlayerState.Paused)
    }

    private fun getCurrentPosition(): Int {
        return playerInteractor.getCurrentPosition()
    }

    private fun setOnCompleteListener() {
        playerInteractor.setOnCompletionListener {
            renderState(PlayerState.Prepared)
            timerLiveData.postValue("00:00")
        }
    }

    fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerState.Playing -> {
                pauseAudioPlayer()
            }

            is PlayerState.Paused -> {
                startAudioPlayer()
                handler.post(updateTime())
            }

            is PlayerState.Prepared -> {
                startAudioPlayer()
                handler.post(updateTime())
            }

            else -> {}
        }
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }

    fun onPause() {
        pauseAudioPlayer()
        handler.removeCallbacksAndMessages(updateTime())
    }


    private fun updateTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (stateLiveData.value is PlayerState.Playing || stateLiveData.value is PlayerState.Paused) {
                    val time = getCurrentPosition()
                    val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                    val formattedTime = timeFormat.format(time)
                    timerLiveData.postValue(formattedTime)
                } else {
                    timerLiveData.postValue("00:00")
                }
                handler.postDelayed(this, PLAYBACK_UPDATE_DELAY_MS)
            }
        }
    }

    private fun isClickAllowed() {
        val current = clickAllowedLiveData.value
        if (current == null || current) {
            clickAllowedLiveData.postValue(false)
            handler.postDelayed({ clickAllowedLiveData.postValue(true) }, CLICK_DEBOUNCE_DELAY_MS)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MS = 2000L
        private const val PLAYBACK_UPDATE_DELAY_MS = 300L
    }
}