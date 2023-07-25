package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private var tracks = mutableListOf<Track>()
    private var history = mutableListOf<Track>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences("prefs_track", MODE_PRIVATE)
        var json = sharedPrefs.getString("TRACKS", "")
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        try {
            history = Gson().fromJson(json, listType)
        } catch (e: Exception) {
            Log.d(TAG, "Empty history")
        }


        val editText = findViewById<EditText>(R.id.inputEditText)
        val searched = findViewById<TextView>(R.id.searched)
        val clearHistory = findViewById<Button>(R.id.clear_history)
        val backImage = findViewById<ImageView>(R.id.backButton)
        val zaglushkaPustoi = findViewById<ImageView>(R.id.zaglushka_pustoi)
        val zaglushkaPustoiText = findViewById<TextView>(R.id.zaglushka_pustoi_text)
        val zaglushkaInetButton = findViewById<Button>(R.id.zaglushka_inet_button)
        val historyAdapter = TrackAdapter(history)
        val trackAdapter = TrackAdapter(tracks)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
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
                    recyclerView.visibility =
                        if (editText.hasFocus() and (s?.isEmpty() == true)) VISIBLE else GONE
                    json = sharedPrefs.getString("TRACKS", "")
                    history = Gson().fromJson(json, listType)
                    recyclerView.adapter = historyAdapter
                    historyAdapter.notifyDataSetChanged()
                }
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
            history = Gson().fromJson(json, listType)
            recyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        }



        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val appleApiService = retrofit.create<AppleApiService>()

        fun searchTracks() {
            recyclerView.visibility = VISIBLE
            recyclerView.adapter = trackAdapter
            appleApiService.search(editText.text.toString())
                .enqueue(object : Callback<TracksResponse> {

                    @SuppressLint("ResourceAsColor")
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                            if (response.body()?.results?.isNotEmpty() == true) {
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
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                            zaglushkaPustoiText.setText(R.string.error404)
                            zaglushkaPustoi.setImageResource(R.drawable.zaglushka_inet)
                            zaglushkaPustoi.visibility = VISIBLE
                            zaglushkaPustoiText.visibility = VISIBLE
                            zaglushkaInetButton.visibility = VISIBLE
                        }
                    }

                })
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

        trackAdapter.setOnTrackClickListener(object : OnTrackClickListener {
            override fun onTrackClick(position: Int) {
                val editor = sharedPrefs.edit()
                if ((history.size <= 9) and !isTrackInHistory(tracks[position])) {
                    history.add(0, tracks[position])
                    json = Gson().toJsonTree(history).asJsonArray.toString()
                    editor.putString("TRACKS", json).apply()
                    Log.d(TAG, "добавили трек в историю (9 и меньше)")
                }
                else if ((history.size == 10) and !isTrackInHistory(tracks[position])) {
                    Log.d(TAG, "добавили трек в историю (10)")
                    history.add(0, tracks[position])
                    history.removeLast()
                    json = Gson().toJsonTree(history).asJsonArray.toString()
                    editor.putString("TRACKS", json).apply()
                }
                else if (isTrackInHistory(tracks[position])) {
                    Log.d(TAG, "трек в истории")
                    lateinit var songbuf: Track
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
    fun isTrackInHistory(track: Track): Boolean {
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



}




