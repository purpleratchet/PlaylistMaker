package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist

interface PlaylistDatabaseInteractor {
    suspend fun insertPlaylistToDatabase(playlist: Playlist)

}