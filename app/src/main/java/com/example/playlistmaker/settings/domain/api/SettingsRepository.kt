package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
    fun setAppTheme()
}