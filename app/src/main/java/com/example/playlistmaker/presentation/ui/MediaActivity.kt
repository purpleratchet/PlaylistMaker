package com.example.playlistmaker.presentation.ui

import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.MediaPresenter
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MediaActivity : AppCompatActivity(), MediaContract {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }


    private var playerState = STATE_DEFAULT
    private lateinit var trackLength: TextView
    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var binding: ActivityMediaBinding
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                trackLength.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }


        val trackName = findViewById<TextView>(R.id.track_name)
        val trackArtist = findViewById<TextView>(R.id.track_artist)
        val trackDuration = findViewById<TextView>(R.id.track_duration)
        val trackAlbum = findViewById<TextView>(R.id.track_album)
        val trackYear = findViewById<TextView>(R.id.track_year)
        val trackGenre = findViewById<TextView>(R.id.track_genre)
        val trackCountry = findViewById<TextView>(R.id.track_country)
        val trackCover = findViewById<ImageView>(R.id.track_cover)
        val album = findViewById<TextView>(R.id.textview2)
        trackLength = findViewById(R.id.track_length)
        val cover = intent.getStringExtra(SearchActivity.EXTRA_TRACK_COVER)!!.replaceAfterLast('/',"512x512bb.jpg")
        Glide.with(this).load(cover)
            .centerCrop()
            .error(R.drawable.zaglushka)
            .placeholder(R.drawable.zaglushka)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corners_cover)))
            .into(trackCover)
        trackName.text = intent.getStringExtra(SearchActivity.EXTRA_TRACK_NAME)
        trackArtist.text = intent.getStringExtra(SearchActivity.EXTRA_ARTIST_NAME)

        val millis = intent.getLongExtra(SearchActivity.EXTRA_TRACK_TIME, 0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
        trackDuration.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        trackAlbum.text = intent.getStringExtra(SearchActivity.EXTRA_COLLECTION_NAME)
        if (trackAlbum.text == "") {
            trackAlbum.visibility = GONE
            album.visibility = GONE
        } else {
            trackAlbum.visibility = VISIBLE
            album.visibility = VISIBLE
        }

        // Предположим, что track.releaseDate представляет собой строку в формате "2017-04-14T12:00:00Z"
        val releaseDateStr = intent.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE) // Здесь track.releaseDate должна быть строкой в правильном формате

// Парсим дату в объект Date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val releaseDate = inputFormat.parse(releaseDateStr!!)

// Теперь, когда у нас есть объект Date, можно его отформатировать
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val formattedDate = outputFormat.format(releaseDate!!)

        trackGenre.text = intent.getStringExtra(SearchActivity.EXTRA_PRIMARY_GENRE_NAME)
        trackCountry.text = intent.getStringExtra(SearchActivity.EXTRA_COUNTRY)
        trackYear.text = formattedDate

        val presenter: MediaContract.Presenter = MediaPresenter(
            binding.playButton,
            binding.pauseButton,
            binding.trackLength,
            intent.getStringExtra(SearchActivity.EXTRA_PREVIEW),
            Creator.createInteractor(intent.getStringExtra(SearchActivity.EXTRA_PREVIEW))
        )

        binding.backButton.setOnClickListener { finish() }
        binding.playButton.setOnClickListener { presenter.onPlayClicked() }
        binding.pauseButton.setOnClickListener { presenter.onPauseAudioClicked() }

    }



}