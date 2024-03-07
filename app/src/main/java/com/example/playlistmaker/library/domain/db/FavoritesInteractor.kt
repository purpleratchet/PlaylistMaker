package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun favoritesTracks(): Flow<List<TrackSearchModel>>
    fun getFavoritesID(): Flow<List<Long>>
    suspend fun addToFavorites(track: TrackSearchModel)
    suspend fun deleteFromFavorites(track: TrackSearchModel)
}