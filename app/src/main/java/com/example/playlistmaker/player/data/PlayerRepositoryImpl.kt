package com.example.playlistmaker.player.data
import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private val player = MediaPlayer()

    override fun setDataSource(dataSource: String) {
        player.setDataSource(dataSource)
    }

    override fun prepareAsync() {
        player.prepareAsync()
    }

    override fun setOnPreparedListener(onPreparedListener: () -> Unit) {
        player.setOnPreparedListener { onPreparedListener() }
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

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }
}