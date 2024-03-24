package com.example.playlistmaker.library.domain.models

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val filePath: String,
    val listOfTracksId: String = "",
    val amountOfTracks: Int
)

fun Playlist.mapToPlaylistEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name,
    description = description,
    filePath = filePath,
    listOfTracksId = listOfTracksId,
    amountOfTracks = amountOfTracks
)

fun PlaylistEntity.mapToPlaylist(): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    filePath = filePath,
    listOfTracksId = listOfTracksId,
    amountOfTracks = amountOfTracks
)