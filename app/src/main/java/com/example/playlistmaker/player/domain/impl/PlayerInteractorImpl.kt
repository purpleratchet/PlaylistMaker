package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository
    : PlayerRepository
) : PlayerInteractor {

    override fun preparePlayer(url: String, onPreparedListener: () -> Unit) {
        playerRepository.preparePlayer(url, onPreparedListener)
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        playerRepository.setOnCompletionListener(onCompletionListener)
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun destroyPlayer() {
        playerRepository.destroyPlayer()
    }
}