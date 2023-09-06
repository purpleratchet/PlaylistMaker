package com.example.playlistmaker.data.dto

import com.example.playlistmaker.data.dto.TrackResult

data class TracksResponse(
    val resultCount: Int,
    val results: List<TrackResult>
    )