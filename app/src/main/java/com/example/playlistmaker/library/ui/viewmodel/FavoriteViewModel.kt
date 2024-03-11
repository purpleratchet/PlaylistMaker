package com.example.playlistmaker.library.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.library.ui.state.FavoritesState
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    application: Application
) : AndroidViewModel(application) {
    private var favoritesState = MutableLiveData<FavoritesState>(FavoritesState.Loading)
    fun observeFavoritesState(): LiveData<FavoritesState> = favoritesState

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoritesInteractor.favoritesTracks()
                .collect { dbTracks ->
                    if (dbTracks.isEmpty()) {
                        favoritesState.postValue(
                            FavoritesState.Empty(
                                getApplication<Application>().getString(R.string.no_favorites)
                            )
                        )
                    } else {
                        favoritesState.postValue(FavoritesState.Content(dbTracks))
                    }
                }
        }
    }
}