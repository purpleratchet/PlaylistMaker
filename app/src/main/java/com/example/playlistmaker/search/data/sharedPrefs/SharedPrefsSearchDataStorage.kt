package com.example.playlistmaker.search.data.sharedPrefs

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.SearchDataStorage
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsSearchDataStorage(
    private val sharedPref: SharedPreferences,
    private val gson: Gson
) : SearchDataStorage {

    override fun addTrackToHistory(item: TrackSearchModel) {
        val sharedPrefsTracks = sharedPref.getString(SAVED_TRACKS, null)
        sharedPrefsTracks?.let { tracksJson ->
            val tracksArray = createTrackListFromJson(tracksJson)
            if (doesTrackExist(item, tracksArray)) {
                tracksArray.remove(item)
            } else if (tracksArray.size >= HISTORY_LIST_SIZE) {
                tracksArray.removeAt(HISTORY_LIST_SIZE - 1)
            }
            tracksArray.add(0, item)
            sharedPref.edit()
                .putString(SAVED_TRACKS, createJsonFromTrackList(tracksArray))
                .apply()
        } ?: run {
            val tracksArray: ArrayList<TrackSearchModel> = arrayListOf(item)
            sharedPref.edit()
                .putString(SAVED_TRACKS, createJsonFromTrackList(tracksArray))
                .apply()
        }
    }

    override fun returnSavedTracks(): ArrayList<TrackSearchModel> {
        val sharedPrefsTracks = sharedPref.getString(SAVED_TRACKS, null)
        return sharedPrefsTracks?.let { createTrackListFromJson(it) } ?: ArrayList()
    }

    override fun clearSavedTracks() {
        sharedPref.edit()
            .remove(SAVED_TRACKS)
            .apply()
    }

    private fun doesTrackExist(
        newTrack: TrackSearchModel,
        trackList: ArrayList<TrackSearchModel>
    ): Boolean {
        return trackList.any { it.trackId == newTrack.trackId }
    }

    private fun createJsonFromTrackList(tracks: ArrayList<TrackSearchModel>): String {
        return gson.toJson(tracks)
    }

    private fun createTrackListFromJson(json: String?): ArrayList<TrackSearchModel> {
        val itemType = object : TypeToken<ArrayList<TrackSearchModel>>() {}.type
        return gson.fromJson(json, itemType)
    }

    companion object {
        const val HISTORY_LIST_SIZE = 10
        const val SAVED_TRACKS = "saved_tracks"
    }
}