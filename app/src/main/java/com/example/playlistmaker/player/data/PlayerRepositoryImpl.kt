package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val player: MediaPlayer
) : PlayerRepository {

    override fun preparePlayer(url: String, onPreparedListener: () -> Unit) {
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            onPreparedListener()
        }
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        player.setOnCompletionListener { onCompletionListener() }
    }

    override fun getCurrentPosition(): Int {
        return player.currentPosition
    }

    override fun startPlayer() {
        player.start()
    }

    override fun pausePlayer() {
        player.pause()
    }

    override fun destroyPlayer() {
        player.release()
    }
}