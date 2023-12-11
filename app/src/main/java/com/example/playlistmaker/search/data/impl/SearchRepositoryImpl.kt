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

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchDataStorage: SearchDataStorage
) : SearchRepository {

    override fun searchTrack(expression: String): ResponseStatus<List<TrackSearchModel>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                ResponseStatus.Error()
            }

            HttpsURLConnection.HTTP_OK -> {
                ResponseStatus.Success((response as TracksSearchResponse).results.map {
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
                })
            }

            else -> {
                ResponseStatus.Error()
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



