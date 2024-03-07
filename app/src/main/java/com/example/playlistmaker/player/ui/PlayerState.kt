package com.example.playlistmaker.player.ui

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val progress: String
) {
    class Default : PlayerState(false, false, "00:00")
    class Prepared : PlayerState(true, false, "00:00")
    class Playing(progress: String) : PlayerState(true, true, progress)
    class Paused(progress: String) : PlayerState(true, false, progress)
}