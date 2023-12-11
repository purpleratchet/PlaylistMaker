package com.example.playlistmaker.search.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.domain.TrackPlayerModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.TracksAdapter
import com.example.playlistmaker.search.ui.model.ScreenState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private val tracks = ArrayList<TrackSearchModel>()
    private val tracksHistory = ArrayList<TrackSearchModel>()
    private val searchAdapter = TracksAdapter(tracks) { trackClickListener(it) }
    private val historyAdapter = TracksAdapter(tracksHistory) { trackClickListener(it) }
    private val handler = Handler(Looper.getMainLooper())
    private var userInput = ""
    private var clickAllowed = true
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        viewModel = ViewModelProvider(
            this, SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        viewModel.stateLiveData().observe(this) {
            updateScreen(it)
        }

        binding.apply {
            rvSearchResult.adapter = searchAdapter
            rvHistory.adapter = historyAdapter
            clearHistoryButton.visibility = View.GONE
            historyMessage.visibility = View.GONE
        }
        binding.btnSearchBack.setOnClickListener {
            finish()
        }
        buttonsConfig()
        queryInputConfig(initTextWatcher())

    }

    private fun initTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.apply {
                clearImageView.visibility = clearButtonVisibility(s)
                rvSearchResult.visibility = View.GONE
            }
            userInput = s.toString()
            viewModel.searchDebounce(userInput, false)
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun buttonsConfig() {
        binding.apply {
            clearImageView.setOnClickListener {
                searchEditText.setText("")
                hideKeyboard()
                tracks.clear()
                viewModel.getTracksHistory()
                searchAdapter.notifyDataSetChanged()
                rvHistory.visibility = View.VISIBLE
                noInternet.visibility = View.GONE
                noResults.visibility = View.GONE
                clearHistoryButton.visibility =
                    if (tracksHistory.isEmpty()) View.GONE else View.VISIBLE
                historyMessage.visibility = if (tracksHistory.isEmpty()) View.GONE else View.VISIBLE
            }
            clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                viewModel.getTracksHistory()
                rvHistory.visibility = View.GONE
                historyMessage.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }

            refresh.setOnClickListener {
                viewModel.searchDebounce(userInput, true)
            }
        }
    }

    private fun queryInputConfig(textWatcher: TextWatcher) {
        binding.searchEditText.apply {
            addTextChangedListener(textWatcher)
            setText(userInput)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && this.text.isEmpty())
                    viewModel.getTracksHistory()
                else
                    binding.rvHistory.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(USER_INPUT, userInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userInput = savedInstanceState.getString(USER_INPUT, "")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun trackClickListener(track: TrackSearchModel) {
        if (isClickAllowed()) {
            val trackPlayerModel = TrackPlayerModel(
                track.trackId,
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
            viewModel.addTrackToHistory(track)
            val playIntent = Intent(this, PlayerActivity::class.java)
                .putExtra(EXTRA_TRACK, trackPlayerModel)
            startActivity(playIntent)
        }
    }


    private fun isClickAllowed(): Boolean {
        val current = clickAllowed
        if (clickAllowed) {
            clickAllowed = false
            handler.postDelayed({ clickAllowed = true }, CLICK_DEBOUNCE_DELAY_MS)
        }
        return current
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateScreen(state: ScreenState) {
        binding.apply {
            when (state) {
                is ScreenState.Content -> {
                    tracks.clear()
                    tracks.addAll(state.tracks as ArrayList<TrackSearchModel>)
                    progressSearch.visibility = View.GONE
                    rvSearchResult.visibility = View.VISIBLE
                    noInternet.visibility = View.GONE
                    noResults.visibility = View.GONE
                    searchAdapter.notifyDataSetChanged()
                }

                is ScreenState.Error -> {
                    progressSearch.visibility = View.GONE
                    noInternet.visibility = View.VISIBLE
                    noResults.visibility = View.GONE
                    imageViewNoInternet.setImageResource(R.drawable.ic_no_internet)
                    textViewNoInternet.setText(R.string.noInternet)
                    refresh.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.GONE
                    historyMessage.visibility = View.GONE
                }

                is ScreenState.Empty -> {
                    progressSearch.visibility = View.GONE
                    noInternet.visibility = View.GONE
                    noResults.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.GONE
                    historyMessage.visibility = View.GONE
                    imageViewNoResults.setImageResource(R.drawable.ic_no_results)
                    textViewNoResults.setText(R.string.NoResults)
                    refresh.visibility = View.GONE
                }

                is ScreenState.Loading -> {
                    noInternet.visibility = View.GONE
                    noResults.visibility = View.GONE
                    rvHistory.visibility = View.GONE
                    rvSearchResult.visibility = View.GONE
                    progressSearch.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    hideKeyboard()
                }

                is ScreenState.ContentHistoryList -> {
                    rvHistory.visibility = View.VISIBLE
                    tracksHistory.clear()
                    tracksHistory.addAll(state.historyList)
                    historyAdapter.notifyDataSetChanged()
                }

                is ScreenState.EmptyHistoryList -> {
                    rvHistory.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    historyMessage.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MS = 2000L
        private const val USER_INPUT = "USER_INPUT"
        private const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}










