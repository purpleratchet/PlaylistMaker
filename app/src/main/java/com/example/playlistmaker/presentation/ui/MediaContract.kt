package com.example.playlistmaker.presentation.ui

interface MediaContract {
    interface Presenter {
        fun onPlayClicked()
        fun onPauseAudioClicked()
        fun onPause()

    }
}