package com.example.playlistmaker.player.domain.api

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    data class Playing(
        val time: Int
    ) : PlayerState

    object Paused : PlayerState
}