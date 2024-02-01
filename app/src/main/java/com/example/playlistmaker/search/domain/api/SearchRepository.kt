package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.ResponseStatus
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchTrack(expression: String): Flow<ResponseStatus<List<TrackSearchModel>>>
    fun getTrackHistoryList(): List<TrackSearchModel>
    fun addTrackInHistory(track: TrackSearchModel)
    fun clearHistory()
}