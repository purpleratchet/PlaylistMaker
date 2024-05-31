package com.example.playlistmaker.search.domain.model
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable
data class TrackSearchModel(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: String,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    val artworkUrl60: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false,
    val insertTimeStamp: Long? = null
) : Serializable
fun TrackSearchModel.mapToPlaylistTrackEntity(newTimeStamp: Boolean = true): PlaylistTrackEntity {
    val timeStamp = if (newTimeStamp) System.currentTimeMillis() else this.insertTimeStamp
    return PlaylistTrackEntity(
        trackId.toInt(),
        trackName,
        artistName,
        trackTimeMillis,
        artworkUrl100,
        artworkUrl60,
        collectionName,
        releaseDate,
        primaryGenreName,
        country,
        previewUrl,
        insertTimeStamp = timeStamp
    )
}

fun PlaylistTrackEntity.mapToTrack(newTimeStamp: Boolean = true): TrackSearchModel {
    val timeStamp = if (newTimeStamp) System.currentTimeMillis() else this.insertTimeStamp
    return TrackSearchModel(
        trackId.toLong(),
        trackName.toString(),
        artistName.toString(),
        trackTime.toString(),
        artworkUrl100.toString(),
        artworkUrl60.toString(),
        collectionName.toString(),
        releaseDate.toString(),
        primaryGenreName.toString(),
        country.toString(),
        previewUrl.toString(),
        insertTimeStamp = timeStamp
    )
}