package com.example.playlistmaker.search.data.sharedPrefs

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.data.SearchDataStorage
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.utils.SEARCH_HISTORY_KEY
import com.example.playlistmaker.utils.SHARED_PREFERENCES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class SharedPrefsSearchDataStorage(context: Context) : SearchDataStorage {

    private val sharedPref = context.getSharedPreferences(
        SHARED_PREFERENCES,
        MODE_PRIVATE
    )

    private val historyList = readFromSharedPref()

    override fun getSearchHistory() = historyList

    override fun clearHistory() {
        historyList.clear()
        updateSharedPref()
    }

    override fun addTrackToHistory(track: TrackDto) {
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size == HISTORY_LIST_SIZE) {
            historyList.removeLast()
        }
        historyList.add(FIRST, track)
        updateSharedPref()
    }

    private fun updateSharedPref() {
        sharedPref.edit().clear().putString(SEARCH_HISTORY_KEY, Gson().toJson(historyList)).apply()
    }

    private fun readFromSharedPref(): ArrayList<TrackDto> {
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return ArrayList()
        val type: Type = object : TypeToken<ArrayList<TrackDto?>?>() {}.type
        return Gson().fromJson<Any>(json, type) as ArrayList<TrackDto>
    }

    companion object {
        private const val HISTORY_LIST_SIZE = 10
        private const val FIRST = 0
    }

}