package com.example.playlistmaker.library.data.impl

import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.models.mapToPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistMediaDatabaseRepositoryImpl(private val playlistDatabase: PlaylistDatabase) :
    PlaylistMediaDatabaseRepository {
    override suspend fun getPlaylistsFromDatabase(): Flow<List<Playlist>> = flow {
        val playlistEntityList = playlistDatabase.playlistDao().getPlaylists()
        emit(playlistEntityList.map { playlistEntity -> playlistEntity.mapToPlaylist() })
    }


}