package com.example.playlistmaker.data

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.domain.Player

class PlayerImpl(private val audioUrl: String?) : Player {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Int = 0
    private var playbackState: PlayerState = PlayerState.DEFAULT

    override fun startAudio() {
        mediaPlayer?.start()
        playbackState = PlayerState.PLAYING
    }

    override fun pauseAudio() {
        mediaPlayer?.pause()
        currentPosition = mediaPlayer?.currentPosition ?: 0
        playbackState = PlayerState.PAUSED
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun currentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun preparePlayer(
        dataSource: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener {
                onPreparedListener()
                playbackState = PlayerState.PREPARED
            }
            setOnCompletionListener {
                onCompletionListener()
                playbackState = PlayerState.DEFAULT
            }
        }
    }

    override fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit) {
        when (playbackState) {
            PlayerState.PLAYING -> {
                onPausePlayer()
                pauseAudio()
                playbackState = PlayerState.PAUSED
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                onStartPlayer()
                startAudio()
                playbackState = PlayerState.PLAYING
            }
            PlayerState.DEFAULT -> {}
        }
    }

    enum class PlayerState {
        DEFAULT, PREPARED, PLAYING, PAUSED
    }
}