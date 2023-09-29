package com.example.playlistmaker.domain.model

data class Track(
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
            return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        }

        fun formatTrackDuration(duration: Long): String {
            val minutes = duration / 60000
            val seconds = (duration % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }

        fun formatReleaseDate(date: String): String {
            return date.substring(0, 4)
        }
    }
}