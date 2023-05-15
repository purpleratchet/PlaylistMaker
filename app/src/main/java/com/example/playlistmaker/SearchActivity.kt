package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Entity
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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

data class Track(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: Int,
    val artworkUrl100: String
)

interface AppleApiService {

    @GET("search?entity=song")
    fun search(@Query("term", encoded = false) text: String): Call<TracksResponse>

}

class SearchActivity : AppCompatActivity() {

    private var tracks = mutableListOf<Track>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val backImage = findViewById<ImageView>(R.id.backButton)
        val zaglushkaPustoi = findViewById<ImageView>(R.id.zaglushka_pustoi)
        val zaglushkaPustoiText = findViewById<TextView>(R.id.zaglushka_pustoi_text)
        val zaglushkaInet = findViewById<ImageView>(R.id.zaglushka_inet)
        val zaglushkaInetText = findViewById<TextView>(R.id.zaglushka_inet_text)
        val zaglushkaInetButton = findViewById<Button>(R.id.zaglushka_inet_button)

        zaglushkaPustoi.visibility = GONE
        zaglushkaPustoiText.visibility = GONE
        zaglushkaInet.visibility = GONE
        zaglushkaInetText.visibility = GONE
        zaglushkaInetButton.visibility = GONE

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
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        val trackAdapter = TrackAdapter(tracks)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = trackAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val appleApiService = retrofit.create<AppleApiService>()


        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                appleApiService.search(editText.text.toString())
                    .enqueue(object : Callback<TracksResponse> {

                        @SuppressLint("ResourceAsColor")
                        override fun onResponse(
                            call: Call<TracksResponse>,
                            response: Response<TracksResponse>
                        ) {
                            if (response.code() == 200) {
                                tracks.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged()
                                } else {
                                    runOnUiThread {
                                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                            zaglushkaPustoi.setImageResource(R.drawable.zaglushka_pustoi_night)
                                            zaglushkaPustoiText.setTextColor(R.color.white)
                                        } else {
                                            zaglushkaPustoi.setImageResource(R.drawable.zaglushka_pustoi)
                                            zaglushkaPustoiText.setTextColor(R.color.black)
                                        }
                                        zaglushkaPustoi.visibility = VISIBLE
                                        zaglushkaPustoiText.visibility = VISIBLE
                                        zaglushkaInet.visibility = GONE
                                        zaglushkaInetText.visibility = GONE
                                        zaglushkaInetButton.visibility = GONE
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                        zaglushkaInet.setImageResource(R.drawable.zaglushka_inet_night)
                                        zaglushkaInetText.setTextColor(R.color.white)
                                        zaglushkaInetButton.setTextColor(R.color.black)
                                        zaglushkaInetButton.setBackgroundColor(R.color.white)
                                    } else {
                                        zaglushkaInet.setImageResource(R.drawable.zaglushka_inet)
                                        zaglushkaInetText.setTextColor(R.color.black)
                                        zaglushkaInetButton.setTextColor(R.color.white)
                                        zaglushkaInetButton.setBackgroundColor(R.color.black)
                                    }
                                    zaglushkaPustoi.visibility = GONE
                                    zaglushkaPustoiText.visibility = GONE
                                    zaglushkaInet.visibility = VISIBLE
                                    zaglushkaInetText.visibility = VISIBLE
                                    zaglushkaInetButton.visibility = VISIBLE
                                }
                            }
                        }

                        @SuppressLint("ResourceAsColor")
                        override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                            runOnUiThread {
                                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                    zaglushkaInet.setImageResource(R.drawable.zaglushka_inet_night)
                                    zaglushkaInetText.setTextColor(R.color.white)
                                    zaglushkaInetButton.setTextColor(R.color.black)
                                    zaglushkaInetButton.setBackgroundColor(R.color.white)
                                } else {
                                    zaglushkaInet.setImageResource(R.drawable.zaglushka_inet)
                                    zaglushkaInetText.setTextColor(R.color.black)
                                    zaglushkaInetButton.setTextColor(R.color.white)
                                    zaglushkaInetButton.setBackgroundColor(R.color.black)
                                }
                                zaglushkaPustoi.visibility = GONE
                                zaglushkaPustoiText.visibility = GONE
                                zaglushkaInet.visibility = VISIBLE
                                zaglushkaInetText.visibility = VISIBLE
                                zaglushkaInetButton.visibility = VISIBLE
                            }
                        }

                    })



                true
            }
            false
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




