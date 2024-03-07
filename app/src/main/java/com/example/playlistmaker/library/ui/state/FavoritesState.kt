package com.example.playlistmaker.library.ui.state

import com.example.playlistmaker.search.domain.model.TrackSearchModel

sealed interface FavoritesState {
    object Loading : FavoritesState

    data class Content(
        val favorites: List<TrackSearchModel>
    ) : FavoritesState

    data class Empty(
        val message: String
    ) : FavoritesState
}