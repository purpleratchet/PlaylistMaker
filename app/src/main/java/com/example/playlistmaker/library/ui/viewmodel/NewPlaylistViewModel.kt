package com.example.playlistmaker.library.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.domain.api.PlaylistDatabaseInteractor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewPlaylistViewModel(
    private val playlistDatabaseInteractor: PlaylistDatabaseInteractor
) : ViewModel() {

    suspend fun insertPlaylistToDatabase(playlist: Playlist) {
        playlistDatabaseInteractor.insertPlaylistToDatabase(playlist)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNameForFile(nameOfPlaylist: String): String {

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)

        val result = nameOfPlaylist.replace(" ", "_")
        return "${result}_${formattedDateTime}.jpg"
    }

}