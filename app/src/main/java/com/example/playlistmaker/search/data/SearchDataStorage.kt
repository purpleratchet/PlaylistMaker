package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.TrackSearchModel

interface SearchDataStorage {
    fun addTrackToHistory(item: TrackSearchModel)
    fun returnSavedTracks(): ArrayList<TrackSearchModel>
    fun clearSavedTracks()

}