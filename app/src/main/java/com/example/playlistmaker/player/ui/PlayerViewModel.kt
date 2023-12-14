package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
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

            is PlayerState.Prepared, PlayerState.Paused -> {
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
                timerLiveData.postValue(
                    SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(getCurrentPosition())
                )
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

        fun getViewModelFactory(url: String): ViewModelProvider.Factory = viewModelFactory() {
            initializer {
                PlayerViewModel(Creator.providePlayerInteractor(), url)
            }
        }
    }
}