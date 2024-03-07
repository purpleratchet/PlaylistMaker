package com.example.playlistmaker.search.ui.model
import com.example.playlistmaker.search.domain.model.TrackSearchModel

sealed interface ScreenState {
    object Loading : ScreenState
    data class SearchedState(val tracks: ArrayList<TrackSearchModel>) : ScreenState
    data class SavedState(val tracks: ArrayList<TrackSearchModel>) : ScreenState
    data class ErrorState(val errorMessage: String) : ScreenState
    data class EmptyState(val message: String) : ScreenState

}