package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.ResponseStatus
import com.example.playlistmaker.search.domain.model.TrackSearchModel

interface SearchRepository {
    fun searchTrack(expression: String): ResponseStatus<List<TrackSearchModel>>
    fun getTrackHistoryList(): List<TrackSearchModel>
    fun addTrackInHistory(track: TrackSearchModel)
    fun clearHistory()
}