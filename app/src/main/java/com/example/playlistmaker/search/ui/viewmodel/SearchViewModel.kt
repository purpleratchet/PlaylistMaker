package com.example.playlistmaker.search.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.model.ScreenState
import com.example.playlistmaker.utils.Debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : AndroidViewModel(application) {
    private val tracks = ArrayList<TrackSearchModel>()
    private val savedTracks = ArrayList<TrackSearchModel>()
    private val _stateLiveData = MutableLiveData<ScreenState>()
    fun observeState(): LiveData<ScreenState> = _stateLiveData
    private val savedTracksLiveData = MutableLiveData<ArrayList<TrackSearchModel>>()
    fun getSavedTracksLiveData(): LiveData<ArrayList<TrackSearchModel>> = savedTracksLiveData
    private var latestSearchText: String? = null

    init {
        viewModelScope.launch {
            savedTracks.addAll(searchInteractor.returnSavedTracks())
        }
    }

    fun isEqual() {
        viewModelScope.launch {
            val idList = favoritesInteractor.getFavoritesID().first()
            tracks.forEach { track ->
                track.isFavorite = idList.contains(track.trackId)
            }
            savedTracks.forEach { track ->
                track.isFavorite = idList.contains(track.trackId)
            }
        }
    }

    private fun renderState(state: ScreenState) {
        _stateLiveData.postValue(state)
    }

    private val debounceHandler = Debounce()

    private val trackSearchDebounce = debounceHandler.debounce<String>(
        SEARCH_DEBOUNCE_DELAY_MS,
        viewModelScope,
        true,
    ) { search(it) }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(ScreenState.Loading)
            viewModelScope.launch {
                searchInteractor.searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<TrackSearchModel>?, errorMessage: String?) {
        foundTracks?.let {
            tracks.clear()
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> renderState(
                ScreenState.ErrorState(
                    getApplication<Application>().getString(
                        R.string.noInternet
                    )
                )
            )

            tracks.isEmpty() -> renderState(
                ScreenState.EmptyState(
                    getApplication<Application>().getString(
                        R.string.NoResults
                    )
                )
            )

            else -> renderState(ScreenState.SearchedState(tracks))
        }
    }

    fun clearHistory() {
        searchInteractor.clearSavedTracks()
        savedTracks.clear()
        _stateLiveData.value = ScreenState.SavedState(savedTracks)
    }

    fun showHistoryTracks() {
        if (savedTracks.size > 0) {
            renderState(ScreenState.SavedState(savedTracks))
        }
    }

    fun addTrackToHistory(track: TrackSearchModel) {
        searchInteractor.addTrackToHistory(track)
        viewModelScope.launch {
            val sharedPrefsTracks = searchInteractor.returnSavedTracks()
            savedTracks.clear()
            savedTracks.addAll(sharedPrefsTracks)
            savedTracksLiveData.postValue(sharedPrefsTracks)
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY_MS = 2000L
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }
}