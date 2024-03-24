package com.example.playlistmaker.player.domain.api


interface PlayerInteractor {
    fun preparePlayer(
        url: String,
        onPreparedPlayer: () -> Unit,
        onCompletionPlayer: () -> Unit
    )


    fun setOnCompletionListener(onCompletionListener: () -> Unit)
    fun seekTo(position: String)
    fun getCurrentPosition(): Int
    fun startPlayer(onStartPlayer: () -> Unit)
    fun pausePlayer(onPausePlayer: () -> Unit)



    fun destroyPlayer()
    fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit)
    fun isPlaying(): Boolean
}