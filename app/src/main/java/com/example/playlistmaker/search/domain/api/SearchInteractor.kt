package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    suspend fun searchTracks(expression: String):
            Flow<Pair<List<TrackSearchModel>?, String?>>
    fun getTracksHistory(consumer: HistoryConsumer)
    fun addTrackToHistory(track: TrackSearchModel)
    fun clearHistory()

    interface HistoryConsumer {
        fun consume(tracks: List<TrackSearchModel>?)
    }
}