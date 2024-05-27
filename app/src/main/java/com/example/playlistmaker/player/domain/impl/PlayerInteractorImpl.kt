package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository
) : PlayerInteractor {
    private var playerState = PlayerState.DEFAULT

    override fun preparePlayer(
        url: String,
        onPreparedPlayer: () -> Unit,
        onCompletionPlayer: () -> Unit
    ) {
        playerRepository.setDataSource(url)
        playerRepository.prepareAsync()
        playerRepository.setOnPreparedListener {
            onPreparedPlayer()
            playerState = PlayerState.PREPARED
        }
        playerRepository.setOnCompletionListener {
            onCompletionPlayer()
            playerState = PlayerState.PREPARED
        }
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        playerRepository.setOnCompletionListener {
            onCompletionListener()
            playerState = PlayerState.PAUSED
        }
    }

    override fun seekTo(position: String) {
        playerRepository.seekTo(position)
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun startPlayer(onStartPlayer: () -> Unit) {
        playerRepository.startPlayer()
        playerState = PlayerState.PLAYING
        onStartPlayer()
    }

    override fun pausePlayer(onPausePlayer: () -> Unit) {
        playerRepository.pausePlayer()
        playerState = PlayerState.PAUSED
        onPausePlayer()
    }

    override fun destroyPlayer() {
        playerRepository.destroyPlayer()
        playerState = PlayerState.DEFAULT
    }

    override fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit) {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer(onPausePlayer)
            }

            PlayerState.PAUSED, PlayerState.PREPARED -> {
                startPlayer(onStartPlayer)
            }

            else -> {}
        }
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    companion object {
        private enum class PlayerState {
            DEFAULT, PREPARED, PLAYING, PAUSED
        }
    }
}