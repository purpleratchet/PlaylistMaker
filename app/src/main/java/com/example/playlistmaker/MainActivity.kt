package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val displayButton = findViewById<Button>(R.id.settings)
        displayButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
        val searchButton = findViewById<Button>(R.id.search)
        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        val mediatekaButton = findViewById<Button>(R.id.mediateka)
        mediatekaButton.setOnClickListener {
            val mediatekaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediatekaIntent)
        }
    }

}
