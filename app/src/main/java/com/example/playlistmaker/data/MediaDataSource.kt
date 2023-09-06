package com.example.playlistmaker.data

import android.content.Intent
import com.example.playlistmaker.presentation.ui.SearchActivity

class MediaDataSource(private val intent: Intent) : MediaRepository {
    override fun getTrackCoverUrl(): String? {
        return intent.getStringExtra(SearchActivity.EXTRA_TRACK_COVER)
    }

    override fun getTrackName(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_TRACK_NAME).toString()
    }

    override fun getArtistName(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_ARTIST_NAME).toString()
    }

    override fun getTrackTime(): String {
        return intent.getLongExtra(SearchActivity.EXTRA_TRACK_TIME, 0).toString()
    }

    override fun getCollectionName(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_COLLECTION_NAME).toString()
    }

    override fun getReleaseDate(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE).toString()
    }

    override fun getPrimaryGenreName(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_PRIMARY_GENRE_NAME).toString()
    }

    override fun getCountry(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_COUNTRY).toString()
    }

    override fun getPreviewUrl(): String {
        return intent.getStringExtra(SearchActivity.EXTRA_PREVIEW).toString()
    }
}