package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private var tracks = mutableListOf<Track>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val backImage = findViewById<ImageView>(R.id.backButton)
        val zaglushkaPustoi = findViewById<ImageView>(R.id.zaglushka_pustoi)
        val zaglushkaPustoiText = findViewById<TextView>(R.id.zaglushka_pustoi_text)
        val zaglushkaInetButton = findViewById<Button>(R.id.zaglushka_inet_button)

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
            }

            override fun afterTextChanged(s: Editable?) {}
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
        }



        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val appleApiService = retrofit.create<AppleApiService>()

        fun searchTracks() {
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




