package com.example.playlistmaker.domain.model

import com.example.playlistmaker.domain.model.Track

data class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
    )