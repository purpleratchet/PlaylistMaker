package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.MediaDataSource
import com.example.playlistmaker.data.MediaRepository
import com.example.playlistmaker.data.dto.TrackResult
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.presentation.MediaContract
import com.example.playlistmaker.presentation.MediaPresenter
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_ARTIST_NAME
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_COLLECTION_NAME
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_COUNTRY
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_PREVIEW
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_PRIMARY_GENRE_NAME
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_RELEASE_DATE
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_TRACK_COVER
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_TRACK_NAME
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.EXTRA_TRACK_TIME

class MediaActivity : AppCompatActivity(), MediaContract {
    private lateinit var presenter: MediaContract.Presenter
    private lateinit var trackLength: TextView
    private lateinit var playButton: ImageView
    private lateinit var binding: ActivityMediaBinding
    private var mediaPlayer: MediaPlayer? = null
    //private lateinit var progressRunnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository: MediaRepository = MediaDataSource(intent)
        presenter = MediaPresenter(
            binding.playButton,
            binding.trackLength,
            intent.getStringExtra(EXTRA_PREVIEW)!!
        )
        playButton = findViewById(R.id.playButton)
        trackLength = findViewById(R.id.track_length)
        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }

        val trackCoverUrl = intent.getStringExtra(EXTRA_TRACK_COVER)
        val trackName = intent.getStringExtra(EXTRA_TRACK_NAME)
        val artistName = intent.getStringExtra(EXTRA_ARTIST_NAME)
        val trackTime = intent.getIntExtra(EXTRA_TRACK_TIME, 0)
        val collectionName = intent.getStringExtra(EXTRA_COLLECTION_NAME)
        val releaseDate = intent.getStringExtra(EXTRA_RELEASE_DATE)
        val primaryGenreName = intent.getStringExtra(EXTRA_PRIMARY_GENRE_NAME)
        val country = intent.getStringExtra(EXTRA_COUNTRY)
        
        binding.trackArtist.text = artistName
        binding.trackName.text = trackName
        binding.trackDuration.text = TrackResult.formatTrackDuration(trackTime)
        binding.trackAlbum.text = collectionName
        binding.trackGenre.text = primaryGenreName
        binding.trackCountry.text = country
        binding.trackYear.text = TrackResult.formatReleaseDate(releaseDate.toString())
        Glide.with(this)
            .load(trackCoverUrl?.let { TrackResult.getCoverArtwork(it) })
            .transform(RoundedCorners(20))
            .placeholder(R.drawable.zaglushka)
            .into(binding.trackCover)
        playButton.setOnClickListener {
            presenter.onPlayClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onPauseClicked()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}