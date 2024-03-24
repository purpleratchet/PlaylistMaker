package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.model.TrackSearchModel

interface PlaylistTrackDatabaseInteractor {
    suspend fun insertTrackToPlaylistTrackDatabase(track: TrackSearchModel)
}