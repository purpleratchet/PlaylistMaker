package com.example.playlistmaker.search.data.impl
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.search.data.SearchDataStorage
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.ResponseStatus
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.net.ssl.HttpsURLConnection
class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchDataStorage: SearchDataStorage,
    private val appDatabase: AppDatabase,
) : SearchRepository {
    override suspend fun searchTracks(expression: String): Flow<ResponseStatus<List<TrackSearchModel>>> =
        flow {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            when (response.resultCode) {
                -1 -> {
                    emit(ResponseStatus.Error())
                }
                HttpsURLConnection.HTTP_OK -> {
                    val favoritesID = appDatabase.trackDao().getTracksID()
                    emit(
                        ResponseStatus.Success((response as TracksSearchResponse).results.map {
                            TrackSearchModel(
                                it.trackId,
                                it.trackName,
                                it.artistName,
                                it.getFormattedDuration(),
                                it.getCoverArtwork(),
                                it.artworkUrl60,
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName!!,
                                it.country,
                                it.previewUrl,
                                favoritesID.contains(it.trackId)
                            )
                        })
                    )
                }
                else -> {
                    emit(ResponseStatus.Error())
                }
            }
        }
    override suspend fun returnSavedTracks(): ArrayList<TrackSearchModel> {
        val favouritesId = appDatabase.trackDao().getTracksID()
        val localTracks = searchDataStorage.returnSavedTracks()
        localTracks.forEach { it.isFavorite = favouritesId.contains(it.trackId) }
        return localTracks
    }
    override fun clearSavedTracks() {
        searchDataStorage.clearSavedTracks()
    }
    override fun addTrackToHistory(item: TrackSearchModel) {
        searchDataStorage.addTrackToHistory(item)
    }
}