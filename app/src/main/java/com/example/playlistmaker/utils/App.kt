package com.example.playlistmaker.utils

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    private var darkTheme = false
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        initTheme()
    }

    fun initTheme() {
        sharedPref = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        if (sharedPref.contains(DARK_THEME_KEY)) {
            darkTheme = sharedPref.getBoolean(DARK_THEME_KEY, false)
            switchTheme(darkTheme)
        } else {
            darkTheme = false
        }
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit().putBoolean(DARK_THEME_KEY, darkThemeEnabled).apply()
    }
}