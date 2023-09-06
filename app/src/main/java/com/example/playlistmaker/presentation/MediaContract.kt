package com.example.playlistmaker.presentation

interface MediaContract {

    interface Presenter {
        fun onPlayClicked()
        fun onPauseClicked()
        fun progressRunnable()
        fun onDestroy()

    }
}