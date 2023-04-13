package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("isDarkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val backImage = findViewById<ImageView>(R.id.backButton)
        backImage.setOnClickListener {
            finish()
        }
        val switcher = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switcher)
        switcher.setOnCheckedChangeListener { _, isChecked ->
            // установка темы в зависимости от состояния Switchmaterial
            val sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putBoolean("isDarkMode", isChecked)
            editor.apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            // перезагрузка активности, чтобы применить новую тему
            val intent = Intent(this, this.javaClass)
            finish()
            startActivity(intent)
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