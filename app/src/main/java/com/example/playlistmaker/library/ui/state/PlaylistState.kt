package com.example.playlistmaker.library.ui.state

import com.example.playlistmaker.library.domain.models.Playlist

sealed class PlaylistState {
    object Loading : PlaylistState()
    class Success(val data: List<Playlist>) : PlaylistState()
}