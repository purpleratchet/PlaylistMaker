package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface CurrentPlaylistRepository {
    suspend fun getTracksForCurrentPlaylist(ids: List<Int>): Flow<List<TrackSearchModel>>
}