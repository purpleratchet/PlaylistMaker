package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(
            R.id.switcher
        )
        themeSwitcher.isChecked = (applicationContext as App).getCurrentTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean("isDarkMode", checked)
                .apply()
        }


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
            shareIntent.putExtra(Intent.EXTRA_EMAIL, R.string.mailAddress)
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