package com.example.playlistmaker.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val iTunesBaseUrl = "https://itunes.apple.com/"
val retrofit: Retrofit =
    Retrofit.Builder().baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
const val SHARED_PREFERENCES = "shared_preferences"
const val THEME_SWITCH_KEY = "theme_key"
const val SEARCH_HISTORY_KEY = "history_key"
const val DARK_THEME_KEY = "darkTheme"