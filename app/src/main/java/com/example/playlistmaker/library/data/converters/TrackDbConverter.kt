package com.example.playlistmaker.library.data.converters
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.TrackSearchModel
class TrackDbConverter {
    fun map(track: TrackSearchModel): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.artworkUrl60,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
            track.insertTimeStamp
        )
    }
    fun map(track: TrackEntity): TrackSearchModel {
        return TrackSearchModel(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.artworkUrl60,
            track.collectionName,
            track.releaseDate!!,
            track.primaryGenreName!!,
            track.country,
            track.previewUrl,
            track.isFavorite,
            track.insertTimeStamp
        )
    }
}