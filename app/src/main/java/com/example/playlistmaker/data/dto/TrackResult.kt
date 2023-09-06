package com.example.playlistmaker.data.dto

import java.util.Date

data class TrackResult(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
    companion object {
        fun getCoverArtwork(artworkUrl100: String): String {
            return artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        }
        fun formatTrackDuration(trackTimeMillis: Int): String {
            val minutes = trackTimeMillis / 1000 / 60
            val seconds = trackTimeMillis / 1000 % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
        fun formatReleaseDate(date: String): String {
            return date.substring(0, 4)
        }
    }
}