package com.example.playlistmaker.search.domain.impl
import com.example.playlistmaker.search.domain.ResponseStatus
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {
    override suspend fun searchTracks(expression: String):
            Flow<Pair<List<TrackSearchModel>?, String?>> {
        return repository.searchTracks(expression).map { resource ->
            when (resource) {
                is ResponseStatus.Success -> {
                    Pair(resource.data, null)
                }

                is ResponseStatus.Error -> {
                    Pair(null, "Ошибка сервера")
                }
            }
        }
    }

    override fun addTrackToHistory(item: TrackSearchModel) {
        repository.addTrackToHistory(item)
    }

    override fun clearSavedTracks() {
        repository.clearSavedTracks()
    }

    override suspend fun returnSavedTracks(): ArrayList<TrackSearchModel> {
        return repository.returnSavedTracks()
    }
}