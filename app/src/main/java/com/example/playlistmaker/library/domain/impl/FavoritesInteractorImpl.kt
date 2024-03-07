package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override fun favoritesTracks(): Flow<List<TrackSearchModel>> {
        return favoritesRepository.favoritesTracks().map { it.reversed() }
    }

    override fun getFavoritesID(): Flow<List<Long>> {
        return favoritesRepository.getFavoritesID()
    }

    override suspend fun addToFavorites(track: TrackSearchModel) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(track: TrackSearchModel) {
        favoritesRepository.deleteFromFavorites(track)
    }
}