package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseInteractor
import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel

class PlaylistTrackDatabaseInteractorImpl(
    private val playlistTrackDatabaseRepository: PlaylistTrackDatabaseRepository
) : PlaylistTrackDatabaseInteractor {
    override suspend fun insertTrackToPlaylistTrackDatabase(track: TrackSearchModel) {
        playlistTrackDatabaseRepository.insertTrackToPlaylistTrackDatabase(track)
    }

    override suspend fun deletePlaylistTrackFromDatabase(track: TrackSearchModel) {
        playlistTrackDatabaseRepository.deletePlaylistTrackFromDatabase(track)
    }

    override suspend fun deletePlaylistTrackFromDatabaseById(id: Int) {
        playlistTrackDatabaseRepository.deletePlaylistTrackFromDatabaseById(id)
    }
}