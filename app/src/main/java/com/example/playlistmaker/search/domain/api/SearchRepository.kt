package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.ResponseStatus

import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchTracks(expression: String):
            Flow<ResponseStatus<List<TrackSearchModel>>>

    suspend fun returnSavedTracks(): ArrayList<TrackSearchModel>
    fun addTrackToHistory(item: TrackSearchModel)
    fun clearSavedTracks()

}