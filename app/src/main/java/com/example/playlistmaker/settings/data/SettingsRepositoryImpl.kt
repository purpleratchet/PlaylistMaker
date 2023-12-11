package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.utils.DARK_THEME_KEY

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {
    override fun getDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }
    override fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, enabled).apply()
    }
    override fun setAppTheme() {
        val darkMode = this.getDarkTheme()
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
