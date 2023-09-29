package com.example.playlistmaker.domain.api

interface MediaRepository {
    fun getTrackCoverUrl(): String?
    fun getTrackName(): String?
    fun getArtistName(): String?
    fun getTrackTime(): String?
    fun getCollectionName(): String?
    fun getReleaseDate(): String?
    fun getPrimaryGenreName(): String?
    fun getCountry(): String?
    fun getPreviewUrl(): String?
}