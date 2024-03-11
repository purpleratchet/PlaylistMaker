package com.example.playlistmaker.search.domain.api
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow
interface SearchInteractor {
    suspend fun searchTracks(expression: String):
            Flow<Pair<List<TrackSearchModel>?, String?>>

    suspend fun returnSavedTracks(): ArrayList<TrackSearchModel>
    fun addTrackToHistory(item: TrackSearchModel)
    fun clearSavedTracks()


}