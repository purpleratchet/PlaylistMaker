package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.player.domain.TrackPlayerModel
import com.example.playlistmaker.player.domain.api.PlayerState
import kotlin.math.roundToInt

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = getTrack()
        bind(track)

        if (track != null) {
            viewModel = ViewModelProvider(
                this, PlayerViewModel.getViewModelFactory(track.previewUrl)
            )[PlayerViewModel::class.java]

            viewModel.observeState().observe(this) {
                updateScreen(it)
            }

            viewModel.observeTimer().observe(this) {
                updateTimer(it)
            }
            viewModel.observeClickAllowed().observe(this) {
                clickAllowed(it)
            }
        }


        binding.btnPlayerBack.setOnClickListener {
            finish()
        }
    }

    private fun getTrack(): TrackPlayerModel? {
        return intent.getSerializableExtra(EXTRA_TRACK) as? TrackPlayerModel
    }

    private fun bind(track: TrackPlayerModel?) {
        track?.let {
            val radius = resources.getDimensionPixelSize(R.dimen.cover_radius).toFloat()
            binding.let {
                Glide.with(this)
                    .load(track.getCoverArtwork())
                    .placeholder(R.drawable.placeholder)
                    .transform(RoundedCorners(radius.roundToInt()))
                    .into(it.trackCover)
            }
            binding.apply {
                trackNameResult.text = it.trackName
                artistNameResult.text = it.artistName
                trackTimeResult.text = it.formatTrackDuration()
                progressTime.text = it.formatTrackDuration()
                collectionName.text = it.collectionName
                releaseDate.text = it.formatReleaseDate()
                primaryGenreName.text = it.primaryGenreName
                country.text = it.country
            }
        }
    }

    private fun updateTimer(time: String) {
        binding.progressTime.text = time
    }

    private fun updateScreen(state: PlayerState) {
        when (state) {
            is PlayerState.Playing -> {
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }

            is PlayerState.Paused -> {
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Prepared -> {
                binding.btnPlay.setImageResource(R.drawable.ic_play)
                binding.progressTime.setText(R.string.default_playtime_value)
            }

            else -> {}
        }
    }

    private fun clickAllowed(isAllowed: Boolean) {
        binding.btnPlay.isEnabled = isAllowed
        binding.btnPause.isEnabled = isAllowed
        viewModel.playbackControl()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}