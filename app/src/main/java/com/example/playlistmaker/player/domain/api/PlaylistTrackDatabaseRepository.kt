package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.model.TrackSearchModel

interface PlaylistTrackDatabaseRepository {
    suspend fun insertTrackToPlaylistTrackDatabase(track: TrackSearchModel)
    suspend fun deletePlaylistTrackFromDatabase(track: TrackSearchModel)

    suspend fun deletePlaylistTrackFromDatabaseById(id: Int)
}