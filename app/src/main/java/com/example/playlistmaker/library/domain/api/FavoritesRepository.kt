package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun favoritesTracks(): Flow<List<TrackSearchModel>>
    fun getFavoritesID(): Flow<List<Long>>
    suspend fun addToFavorites(track: TrackSearchModel)
    suspend fun deleteFromFavorites(track: TrackSearchModel)
}