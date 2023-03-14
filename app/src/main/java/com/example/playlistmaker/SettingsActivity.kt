package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }
//        val themeChanger = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switcher)
//        themeChanger.setOnClickListener {
//           onNightModeChanged(AppCompatDelegate.MODE_NIGHT_YES)
//        }
//        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        when (currentNightMode) {
//            Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme.
//            Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme.
//        }
        val shareApp = findViewById<TextView>(R.id.textView3)
        shareApp.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.linkCourse))
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Share"))
        }
        val supportApp = findViewById<TextView>(R.id.textView4)
        supportApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("danparkin@yandex.ru"))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.emailText))
            startActivity(Intent.createChooser(shareIntent,"Support"))
        }
        val policyApp = findViewById<TextView>(R.id.textView5)
        policyApp.setOnClickListener {
            val policyIntent = Intent(Intent.ACTION_VIEW)
            policyIntent.data = Uri.parse(getString(R.string.linkLegacy))
            startActivity(Intent.createChooser(policyIntent, "Policy"))
        }
    }
}