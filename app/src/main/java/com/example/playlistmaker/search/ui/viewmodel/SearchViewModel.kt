package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.model.ScreenState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private var clickAllowed = true
    private val _stateLiveData = MutableLiveData<ScreenState>()
    fun stateLiveData(): LiveData<ScreenState> = _stateLiveData

    private var latestSearchText: String? = null

    private var searchJob: Job? = null
    fun searchDebounce(changedText: String, hasError: Boolean) {
        if (latestSearchText == changedText && !hasError) {
            return
        }
        latestSearchText = changedText
        debouncedSearch(changedText)
    }

    val debouncedSearch: (String) -> Unit = debounce(
        SEARCH_DEBOUNCE_DELAY_MS,
        viewModelScope,
        true
    ) { changedText ->
        search(changedText)
    }

    private fun search(expression: String) {
        if (expression.isNotEmpty()) {
            renderState(ScreenState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(expression)
                    .collect { pair ->
                        val tracks = mutableListOf<TrackSearchModel>()
                        if (pair.first != null) {
                            tracks.addAll(pair.first!!)
                            when {
                                tracks.isEmpty() -> renderState(ScreenState.Empty())
                                else -> renderState(ScreenState.Content(tracks))
                            }

                    } else {
                            renderState(ScreenState.Error())
                        }}}
        }
    }

    fun getTracksHistory() {
        searchInteractor.getTracksHistory(object : SearchInteractor.HistoryConsumer {
            override fun consume(tracks: List<TrackSearchModel>?) {
                if (tracks.isNullOrEmpty()) {
                    renderState(ScreenState.EmptyHistoryList())
                } else {
                    renderState(ScreenState.ContentHistoryList(tracks))
                }
            }
        })
    }

    fun addTrackToHistory(track: TrackSearchModel) {
        searchInteractor.addTrackToHistory(track)
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
    }

    private fun renderState(state: ScreenState) {
        _stateLiveData.postValue(state)
    }

    private fun isClickAllowed(): Boolean {
        val current = clickAllowed
        if (clickAllowed) {
            clickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MS)
                clickAllowed = true
            }
        }
        return current
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 2000L
        private const val CLICK_DEBOUNCE_DELAY_MS = 500L
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}