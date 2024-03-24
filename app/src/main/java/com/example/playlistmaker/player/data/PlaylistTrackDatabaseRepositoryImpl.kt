package com.example.playlistmaker.player.data

import com.example.playlistmaker.library.data.db.PlaylistTrackDatabase
import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.domain.model.mapToPlaylistTrackEntity


class PlaylistTrackDatabaseRepositoryImpl(
    private val playlistTrackDatabase: PlaylistTrackDatabase
) : PlaylistTrackDatabaseRepository {

    override suspend fun insertTrackToPlaylistTrackDatabase(track: TrackSearchModel) {
        playlistTrackDatabase.playlistTrackDao().insertTrack(track.mapToPlaylistTrackEntity())
    }
}