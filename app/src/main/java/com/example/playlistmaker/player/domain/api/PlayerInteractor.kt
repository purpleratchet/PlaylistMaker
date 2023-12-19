package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String, onPreparedListener: () -> Unit)

    fun setOnCompletionListener(onCompletionListener: () -> Unit)

    fun getCurrentPosition(): Int

    fun startPlayer()

    fun pausePlayer()

    fun destroyPlayer()

}