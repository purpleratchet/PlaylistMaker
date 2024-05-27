package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistMediaDatabaseRepository {
    suspend fun getPlaylistsFromDatabase(): Flow<List<Playlist>>
    suspend fun deletePlaylist(playlist: Playlist)
}