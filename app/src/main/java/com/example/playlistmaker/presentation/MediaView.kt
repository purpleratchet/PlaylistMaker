package com.example.playlistmaker.presentation

interface MediaView {
    fun updateProgressTime(time: String)
    fun showPlayButton()
    fun showPauseButton()
}