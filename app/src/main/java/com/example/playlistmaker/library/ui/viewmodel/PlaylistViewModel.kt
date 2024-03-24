package com.example.playlistmaker.library.ui.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.state.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistMediaDatabaseInteractor: PlaylistMediaDatabaseInteractor
) : ViewModel() {

    private var _databasePlaylistState = MutableLiveData<PlaylistState>()
    var databasePlaylistState: LiveData<PlaylistState> = _databasePlaylistState

    fun fillData() {

        _databasePlaylistState.postValue(
            PlaylistState.Loading
        )

        viewModelScope.launch {
            playlistMediaDatabaseInteractor
                .getPlaylistsFromDatabase()
                .collect {listOfPlaylists ->
                    processResult(listOfPlaylists)
                }
        }

    }
    private fun processResult(listOfPlaylists: List<Playlist>) {
        _databasePlaylistState.postValue(
            PlaylistState.Success(listOfPlaylists)
        )
    }

}