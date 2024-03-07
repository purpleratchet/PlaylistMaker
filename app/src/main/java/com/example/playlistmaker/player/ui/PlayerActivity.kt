package com.example.playlistmaker.player.ui

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.viewModel.PlayerViewModel
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            playButton = btnPlay
            timer = progressTime
        }

        intent?.let {
            val track = intent.extras?.getSerializable(EXTRA_TRACK) as TrackSearchModel
            val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }
            preparePlayer(track)

            viewModel.observePlayerState.observe(this) {
                playButton.isEnabled = it.isPlayButtonEnabled
                timer.text = it.progress
                playButton.setImageResource(playButtonImage(it.isPlaying))
            }

            viewModel.observeIsFavorite.observe(this) { isFavorite ->
                val dislikeIcon: Int by lazy {
                    if (isDarkTheme()) R.drawable.ic_dislike_dark else R.drawable.ic_dislike
                }
                val favoriteIcon: Int by lazy {
                    if (isDarkTheme()) R.drawable.ic_favorite_dark else R.drawable.ic_favorite
                }
                binding.btnFavorite.setImageResource(if (isFavorite) dislikeIcon else favoriteIcon)
                binding.btnPlayerBack.setOnClickListener { super.onBackPressed() }
                binding.btnFavorite.setOnClickListener { viewModel.onFavoriteClicked() }

                playButton.setOnClickListener {
                    viewModel.playbackControl()
                }
            }
        }
    }

    private fun playButtonImage(isPlaying: Boolean): Int {
        val playIcon = if (isDarkTheme()) R.drawable.ic_play_dark else R.drawable.ic_play
        val pauseIcon = if (isDarkTheme()) R.drawable.ic_pause_dark else R.drawable.ic_pause
        return if (isPlaying) pauseIcon else playIcon
    }

    private fun isDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun preparePlayer(track: TrackSearchModel) {
        binding.apply {
            trackNameResult.text = track.trackName
            artistNameResult.text = track.artistName
            trackTimeResult.text = track.trackTimeMillis.toString()
            collectionName.text = track.collectionName
            releaseDate.text = track.releaseDate.substring(0, 4)
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country
            progressTime.text = getString(R.string.default_playtime_value)
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(binding.trackCover)
    }

    companion object {
        private const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}