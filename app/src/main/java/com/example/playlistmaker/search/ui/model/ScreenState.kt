package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.TrackSearchModel

sealed interface ScreenState {

    object Loading : ScreenState

    data class Content(val tracks: List<TrackSearchModel>) : ScreenState

    class Error : ScreenState

    class Empty : ScreenState

    data class ContentHistoryList(val historyList: List<TrackSearchModel>) : ScreenState

    class EmptyHistoryList() : ScreenState

}