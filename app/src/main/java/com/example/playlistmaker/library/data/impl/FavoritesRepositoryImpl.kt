package com.example.playlistmaker.library.data.impl

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoritesRepository {
    override fun favoritesTracks(): Flow<List<TrackSearchModel>> = flow {
        val movies = withContext(Dispatchers.IO) { appDatabase.trackDao().getFavoriteTracks() }
        emit(convertFromTracksEntity(movies))
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<TrackSearchModel> {
        return tracks.map { tracks -> trackDbConverter.map(tracks) }
    }
    override suspend fun addToFavorites(track: TrackSearchModel) {
        saveTrack(track)
    }

    override suspend fun deleteFromFavorites(track: TrackSearchModel) {
        deleteTrack(track)
    }

    override fun getFavoritesID(): Flow<List<Long>> = flow {
        val tracksId = withContext(Dispatchers.IO) { appDatabase.trackDao().getTracksID() }
        emit(tracksId)
    }

    private suspend fun saveTrack(track: TrackSearchModel) {
        val trackEntity = trackDbConverter.map(track)
        withContext(Dispatchers.IO) { appDatabase.trackDao().insertToFavorites(trackEntity) }
    }

    private suspend fun deleteTrack(track: TrackSearchModel) {
        val trackEntity = trackDbConverter.map(track)
        withContext(Dispatchers.IO) { appDatabase.trackDao().deleteFromFavorites(trackEntity) }
    }
}