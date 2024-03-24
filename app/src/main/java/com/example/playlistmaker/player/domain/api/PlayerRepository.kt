package com.example.playlistmaker.player.domain.api

interface PlayerRepository {
    fun setDataSource(dataSource: String)
    fun prepareAsync()
    fun setOnPreparedListener(onPreparedListener: () -> Unit)
    fun setOnCompletionListener(onCompletionListener: () -> Unit)
    fun getCurrentPosition(): Int
    fun startPlayer()
    fun pausePlayer()
    fun destroyPlayer()
    fun isPlaying(): Boolean
    fun seekTo(position: String)
}