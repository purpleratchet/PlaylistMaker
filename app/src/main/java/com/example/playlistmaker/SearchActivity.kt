package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

data class Track(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val editText = findViewById<EditText>(R.id.inputEditText)
        val backImage = findViewById<ImageView>(R.id.backButton)
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
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        val trackAdapter = TrackAdapter(tracks)
        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = trackAdapter

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

    private var tracks = mutableListOf<Track>()

    init {

        tracks = (1..5).map {
            Track(
                id = it.toLong(),
                trackName = TRACKNAMES[it-1],
                artistName = ARTISTS[it-1],
                trackTime = LENGTHS[it-1],
                artworkUrl100 = IMAGES[it-1]
            )
        }.toMutableList()
    }

    companion object {
        private val IMAGES = mutableListOf(
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
        private val TRACKNAMES = mutableListOf(
            "Smells Like Teen Spirit",
            "Billie Jean",
            "Stayin' Alive",
            "Whole Lotta Love",
            "Sweet Child O'Mine"
        )
        private val ARTISTS = mutableListOf(
            "Nirvana",
            "Michael Jackson",
            "Bee Gees",
            "Led Zeppelin",
            "Guns N' Roses"
        )
        private val LENGTHS = mutableListOf(
            "5:01", "4:35", "4:10", "5:33", "5:03"
        )
    }
}



