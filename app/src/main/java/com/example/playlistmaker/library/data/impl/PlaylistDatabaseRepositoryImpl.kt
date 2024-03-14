package com.example.playlistmaker.library.data.impl

import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.api.PlaylistDatabaseRepository
import com.example.playlistmaker.library.domain.models.mapToPlaylistEntity

class PlaylistDatabaseRepositoryImpl(private val playlistDatabase: PlaylistDatabase) :
    PlaylistDatabaseRepository {
    override suspend fun insertPlaylistToDatabase(playlist: Playlist) {
        playlistDatabase.playlistDao().insertPlaylist(playlist.mapToPlaylistEntity())
    }

}