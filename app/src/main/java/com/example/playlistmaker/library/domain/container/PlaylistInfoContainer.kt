package com.example.playlistmaker.library.domain.container
import com.example.playlistmaker.search.domain.model.TrackSearchModel
data class PlaylistInfoContainer(
    val totalTime: String,
    val playlistTracks: List<TrackSearchModel>
)
