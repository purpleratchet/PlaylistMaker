package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: TrackSearchModel,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private var timerJob: Job? = null
    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val observePlayerState: LiveData<PlayerState> = playerState
    private val isFavorite = MutableLiveData(track.isFavorite)
    val observeIsFavorite: LiveData<Boolean> = isFavorite

    init {
        playerInteractor.preparePlayer(
            track.previewUrl,
            { playerState.postValue(PlayerState.Prepared()) },
            { playerState.postValue(PlayerState.Prepared()) })
        playerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        }
    }

    fun playbackControl() {
        playerInteractor.playbackControl(
            startAudioPlayer(),
            pauseAudioPlayer()
        )
    }

    fun onFavoriteClicked() {
        if (isFavorite.value == false) {
            track.isFavorite = true
            viewModelScope.launch {
                favoritesInteractor.addToFavorites(track)
            }
            isFavorite.postValue(true)
        } else {
            track.isFavorite = false
            viewModelScope.launch {
                favoritesInteractor.deleteFromFavorites(track)
            }
            isFavorite.postValue(false)
        }
    }

    private fun startAudioPlayer(): () -> Unit = {
        playerInteractor.startPlayer {
            playerState.postValue(PlayerState.Playing(getCurrentPosition()))
            updateTime()
        }
    }

    private fun pauseAudioPlayer(): () -> Unit = {
        playerInteractor.pausePlayer {
            timerJob?.cancel()
            if (playerState.value is PlayerState.Playing) {
                playerState.postValue(PlayerState.Paused(getCurrentPosition()))
            }
        }
    }

    private val timeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }
    private fun getCurrentPosition(): String {
        return timeFormat.format(playerInteractor.getCurrentPosition())
    }

    override fun onCleared() {
        super.onCleared()
        releaseAudioPlayer()
    }

    private fun updateTime() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(PLAYBACK_UPDATE_DELAY_MS)
                playerState.postValue(PlayerState.Playing(getCurrentPosition()))
            }
        }
    }

    private fun releaseAudioPlayer() {
        playerInteractor.destroyPlayer()
        playerState.value = PlayerState.Default()
    }

    companion object {
        private const val PLAYBACK_UPDATE_DELAY_MS = 300L
    }
}