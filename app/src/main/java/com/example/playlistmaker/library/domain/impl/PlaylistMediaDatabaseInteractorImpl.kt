package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseInteractor
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseRepository
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistMediaDatabaseInteractorImpl(
    private val playlistMediaDatabaseRepository: PlaylistMediaDatabaseRepository
) : PlaylistMediaDatabaseInteractor {
    override suspend fun getPlaylistsFromDatabase(): Flow<List<Playlist>> {
        return playlistMediaDatabaseRepository.getPlaylistsFromDatabase()
    }
    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistMediaDatabaseRepository.deletePlaylist(playlist)
    }
}