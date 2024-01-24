package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.SearchDataStorage
import com.example.playlistmaker.search.domain.ResponseStatus
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchDataStorage: SearchDataStorage
) : SearchRepository {

    override suspend fun searchTrack(expression: String): Flow<ResponseStatus<List<TrackSearchModel>>> =
        flow {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            when (response.resultCode) {
                -1 -> {
                    emit(ResponseStatus.Error())
                }
                HttpsURLConnection.HTTP_OK -> {
                    with(response as TracksSearchResponse) {
                        val data = results.map {
                            TrackSearchModel(
                                it.trackId,
                                it.trackName,
                                it.artistName,
                                it.trackTimeMillis,
                                it.artworkUrl100,
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName,
                                it.country,
                                it.previewUrl
                            )
                        }
                        emit(ResponseStatus.Success(data))
                    }
                }

            else -> {
                emit(ResponseStatus.Error())
            }
        }
    }

    override fun getTrackHistoryList(): List<TrackSearchModel> {
        return searchDataStorage.getSearchHistory().map {
            TrackSearchModel(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    override fun addTrackInHistory(track: TrackSearchModel) {
        searchDataStorage.addTrackToHistory(
            TrackDto(
                track.trackId,
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        )
    }

    override fun clearHistory() {
        searchDataStorage.clearHistory()
    }
}



