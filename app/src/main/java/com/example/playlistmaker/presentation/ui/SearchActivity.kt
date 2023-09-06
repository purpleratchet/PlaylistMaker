package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.OnTrackClickListener
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.data.dto.TrackResult
import com.example.playlistmaker.data.network.AppleApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private var tracks = mutableListOf<TrackResult>()
    private var history = mutableListOf<TrackResult>()
    private lateinit var editText: EditText
    private lateinit var searched: TextView
    private lateinit var clearHistory: Button
    private lateinit var backImage: ImageView
    private lateinit var zaglushkaPustoi: ImageView
    private lateinit var zaglushkaPustoiText: TextView
    private lateinit var zaglushkaInetButton: Button
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var appleApiService = retrofit.create<AppleApiService>()

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val EXTRA_TRACK_ID = "trackId"
        const val EXTRA_TRACK_NAME = "trackName"
        const val EXTRA_ARTIST_NAME = "artistName"
        const val EXTRA_TRACK_TIME = "trackTimeMillis"
        const val EXTRA_TRACK_COVER = "trackCover"
        const val EXTRA_COLLECTION_NAME = "collectionName"
        const val EXTRA_RELEASE_DATE = "releaseDate"
        const val EXTRA_PRIMARY_GENRE_NAME = "primaryGenreName"
        const val EXTRA_COUNTRY = "country"
        const val EXTRA_PREVIEW = "previewUrl"
    }
    private val searchRunnable = Runnable { searchTracks() }
    private val handler = Handler(Looper.getMainLooper())
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences("prefs_track", MODE_PRIVATE)
        var json = sharedPrefs.getString("TRACKS", "")
        val listType = object : TypeToken<MutableList<TrackResult>>() {}.type
        try {
            history = Gson().fromJson(json, listType)
            Log.d(TAG, history[0].toString())
        } catch (e: Exception) {
            Log.d(TAG, "Empty history")
        }

        editText = findViewById(R.id.inputEditText)
        searched = findViewById(R.id.searched)
        clearHistory = findViewById(R.id.clear_history)
        backImage = findViewById(R.id.backButton)
        zaglushkaPustoi = findViewById(R.id.zaglushka_pustoi)
        zaglushkaPustoiText = findViewById(R.id.zaglushka_pustoi_text)
        zaglushkaInetButton = findViewById(R.id.zaglushka_inet_button)
        historyAdapter = TrackAdapter(history)
        trackAdapter = TrackAdapter(tracks)
        recyclerView = findViewById(R.id.recycler)
        progressBar = findViewById(R.id.progressBar)


        appleApiService = retrofit.create()

        recyclerView.adapter = trackAdapter

        backImage.setOnClickListener {
            finish()
        }



        val clearButton = findViewById<ImageView>(R.id.clear_text)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (history.size > 0) {
                    Log.d(TAG, "мы в фокусе текствотч")
                    searched.visibility = if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    clearHistory.visibility =
                        if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    recyclerView.visibility = if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    json = sharedPrefs.getString("TRACKS", "")
                   history = Gson().fromJson(json, listType)
                    Log.d(TAG, history[0].toString())
                    recyclerView.adapter = historyAdapter
                    historyAdapter.notifyDataSetChanged()
               }
               if (s!!.isNotEmpty()) searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (history.size > 0) {
                    Log.d(TAG, "мы в фокусе текствотч")
                    searched.visibility = if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    clearHistory.visibility =
                        if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    recyclerView.visibility =
                        if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    json = sharedPrefs.getString("TRACKS", "")
                    history = Gson().fromJson(json, listType)
                    Log.d(TAG, history[0].toString())
                    recyclerView.adapter = historyAdapter
                    historyAdapter.notifyDataSetChanged()
                }
            }
        })
        clearButton.setOnClickListener {
            editText.setText("")
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            zaglushkaPustoi.visibility = GONE
            zaglushkaPustoiText.visibility = GONE
            zaglushkaInetButton.visibility = GONE
            json = sharedPrefs.getString("TRACKS", "")
            if (json != "") history = Gson().fromJson(json, listType)
            try {Log.d(TAG, history[0].toString())} catch (e: NullPointerException) { Log.d(TAG, "null") }
            recyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                searchTracks()
                true
            }
            false
        }

        zaglushkaInetButton.setOnClickListener {
            zaglushkaPustoi.visibility = GONE
            zaglushkaPustoiText.visibility = GONE
            zaglushkaInetButton.visibility = GONE
            searchTracks()
        }

        historyAdapter.setOnTrackClickListener(object : OnTrackClickListener {
            override fun onTrackClick(position: Int) {
                if (clickDebounce()) {
                    val intent = Intent(this@SearchActivity, MediaActivity::class.java).apply {
                        putExtra(EXTRA_TRACK_ID, history[position].trackId)
                        putExtra(EXTRA_TRACK_NAME, history[position].trackName)
                        putExtra(EXTRA_ARTIST_NAME, history[position].artistName)
                        putExtra(EXTRA_TRACK_TIME, history[position].trackTimeMillis)
                        putExtra(EXTRA_TRACK_COVER, history[position].artworkUrl100)
                        putExtra(EXTRA_COLLECTION_NAME, history[position].collectionName)
                        putExtra(EXTRA_RELEASE_DATE, history[position].releaseDate)
                        putExtra(EXTRA_PRIMARY_GENRE_NAME, history[position].primaryGenreName)
                        putExtra(EXTRA_COUNTRY, history[position].country)
                        putExtra(EXTRA_PREVIEW, history[position].previewUrl)
                    }
                    Log.e(TAG, history[position].toString())
                    startActivity(intent)
                }
            }
        })

        trackAdapter.setOnTrackClickListener(object : OnTrackClickListener {
            override fun onTrackClick(position: Int) {
                if (clickDebounce()) {
                    val editor = sharedPrefs.edit()
                    if ((history.size <= 9) and !isTrackInHistory(tracks[position])) {
                        history.add(0, tracks[position])
                        json = Gson().toJsonTree(history).asJsonArray.toString()
                        editor.putString("TRACKS", json).apply()
                        Log.d(TAG, "добавили трек в историю (9 и меньше)")
                    } else if ((history.size == 10) and !isTrackInHistory(tracks[position])) {
                        Log.d(TAG, "добавили трек в историю (10)")
                        history.add(0, tracks[position])
                        history.removeLast()
                        json = Gson().toJsonTree(history).asJsonArray.toString()
                        editor.putString("TRACKS", json).apply()
                    } else if (isTrackInHistory(tracks[position])) {
                        Log.d(TAG, "трек в истории")
                        lateinit var songbuf: TrackResult
                        for (song in history) {
                            if (song.trackId == tracks[position].trackId) {
                                songbuf = song
                            }
                        }
                        history.remove(songbuf)
                        history.add(0, tracks[position])
                        json = Gson().toJsonTree(history).asJsonArray.toString()
                        editor.putString("TRACKS", json).apply()
                    }

                    val trackForMedia = getSharedPreferences("prefs_track", MODE_PRIVATE)
                    val trackJson = Gson().toJson(tracks[position])
                    trackForMedia.edit()
                        .putString("MEDIA", trackJson.toString())
                        .apply()
                    val displayIntent = Intent(applicationContext, MediaActivity::class.java)
                    startActivity(displayIntent)
                }
            }
        })
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (history.size > 0) {
                Log.d(TAG, "мы в фокусе сете")

                searched.visibility = if (hasFocus and editText.text.isEmpty()) VISIBLE else GONE
                clearHistory.visibility =
                    if (hasFocus and editText.text.isEmpty()) VISIBLE else GONE
                recyclerView.visibility = if (hasFocus and editText.text.isEmpty()) VISIBLE else GONE
                json = sharedPrefs.getString("TRACKS", "")
                history = Gson().fromJson(json, listType)
                recyclerView.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()

            }
        }
        clearHistory.setOnClickListener {
            history.clear()
            historyAdapter.notifyDataSetChanged()
            sharedPrefs.edit().putString("TRACKS", "").apply()
            searched.visibility = GONE
            clearHistory.visibility = GONE
            recyclerView.visibility = GONE
        }

    }
    fun isTrackInHistory(track: TrackResult): Boolean {
        for (song in history) {
            if (track.trackId == song.trackId) return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val editText = findViewById<EditText>(R.id.inputEditText)
        outState.putString("myKey", editText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val myValue = savedInstanceState.getString("myKey")
        val editText = findViewById<EditText>(R.id.inputEditText)
        editText.setText(myValue)
    }

    fun searchTracks() {
        progressBar.visibility = VISIBLE
        recyclerView.visibility = GONE
        zaglushkaPustoi.visibility = GONE
        zaglushkaPustoiText.visibility = GONE
        zaglushkaInetButton.visibility = GONE
        recyclerView.adapter = trackAdapter
        if (editText.text.isNotEmpty()) {
            appleApiService.search(editText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    @SuppressLint("ResourceAsColor")
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        progressBar.visibility = GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                recyclerView.visibility = VISIBLE
                                zaglushkaPustoi.visibility = GONE
                                zaglushkaPustoiText.visibility = GONE
                                zaglushkaInetButton.visibility = GONE
                                tracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            } else {
                                runOnUiThread {
                                    tracks.clear()
                                    trackAdapter.notifyDataSetChanged()
                                    zaglushkaPustoiText.setText(R.string.error_not_found)
                                    zaglushkaPustoi.setImageResource(R.drawable.zaglushka_pustoi)
                                    zaglushkaPustoi.visibility = VISIBLE
                                    zaglushkaPustoiText.visibility = VISIBLE
                                    zaglushkaInetButton.visibility = GONE
                                }
                            }
                        } else {
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                            runOnUiThread {
                                zaglushkaPustoiText.setText(R.string.error404)
                                zaglushkaPustoi.setImageResource(R.drawable.zaglushka_inet)
                                zaglushkaPustoi.visibility = VISIBLE
                                zaglushkaPustoiText.visibility = VISIBLE
                                zaglushkaInetButton.visibility = VISIBLE
                            }
                        }
                    }

                    @SuppressLint("ResourceAsColor")
                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        runOnUiThread {
                            progressBar.visibility = GONE

                            zaglushkaPustoiText.setText(R.string.error404)
                            zaglushkaPustoi.setImageResource(R.drawable.zaglushka_inet)
                            zaglushkaPustoi.visibility = VISIBLE
                            zaglushkaPustoiText.visibility = VISIBLE
                            zaglushkaInetButton.visibility = VISIBLE
                        }
                    }

                })
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}




