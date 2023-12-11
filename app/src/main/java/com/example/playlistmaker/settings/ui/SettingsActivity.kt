package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator

class SettingsActivity : AppCompatActivity() {
    private lateinit var switch: SwitchCompat
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        switch = findViewById(R.id.themeSwitcher)

        val viewModelFactory =
            Creator.createSettingsViewModelFactory(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

        viewModel.getDarkThemeLiveData().observe(this) { isDarkTheme ->
            switch.isChecked = isDarkTheme
            viewModel.setAppTheme()
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
            viewModel.setAppTheme()
        }

        val savedDarkTheme =
            viewModel.getDarkTheme()
        switch.isChecked = savedDarkTheme

        val backButton = findViewById<Button>(R.id.btnSettingsBack)
        backButton.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setAppTheme()
    }

    fun onShareClick(view: View) {
        viewModel.shareApp()
    }

    fun onSupportClick(view: View) {
        viewModel.sendSupportEmail()
    }

    fun onAgreementClick(view: View) {
        viewModel.openAgreementUrl()
    }

}





