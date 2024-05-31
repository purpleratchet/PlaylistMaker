package com.example.playlistmaker.library.ui.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.container.PlaylistInfoContainer
import com.example.playlistmaker.library.domain.api.CurrentPlaylistInteractor
import com.example.playlistmaker.library.domain.api.PlaylistDatabaseInteractor
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseInteractor
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val currentPlaylistInteractor: CurrentPlaylistInteractor,
    private val playlistDatabaseInteractor: PlaylistDatabaseInteractor,
    private val playlistTrackDatabaseInteractor: PlaylistTrackDatabaseInteractor,
    private val playlistMediaDatabaseInteractor: PlaylistMediaDatabaseInteractor
) : ViewModel() {
    var updatedPlaylist: Playlist? = null
    val listOfCurrentTracks = ArrayList<TrackSearchModel>()
    private val _tracksForCurrentPlaylist = MutableLiveData<PlaylistInfoContainer>()
    val tracksForCurrentPlaylist: LiveData<PlaylistInfoContainer> = _tracksForCurrentPlaylist
    fun getTracksFromDatabaseForCurrentPlaylist(ids: List<Int>) {
        viewModelScope.launch {
            currentPlaylistInteractor
                .getTracksForCurrentPlaylist(ids)
                .collect { tracksForCurrentPlaylist ->
                    val listOfTotalTime = tracksForCurrentPlaylist.map { track ->
                        val timeComponents = track.trackTimeMillis?.split(":") ?: emptyList()
                        val duration = timeComponents
                            .map { component -> component.toIntOrNull() ?: 0 }
                            .takeLast(3) // Оставляем только минуты, секунды и миллисекунды
                        val totalMilliseconds = when (duration.size) {
                            3 -> duration[0] * 60 * 60 * 1000 + duration[1] * 60 * 1000 + duration[2] * 1000
                            2 -> duration[0] * 60 * 1000 + duration[1] * 1000
                            else -> 0
                        }
                        totalMilliseconds
                    }

                    val sum = listOfTotalTime.sum()
                    val sumInMinutes = sum / (60 * 1000)
                    val reversedIds = ids.reversed()
                    val sortedTracks = tracksForCurrentPlaylist
                        .sortedBy { track -> reversedIds.indexOf(track.trackId.toInt()) }
                    _tracksForCurrentPlaylist.postValue(
                        PlaylistInfoContainer(
                            totalTime = pluralizeWord(sumInMinutes, "минута"),
                            playlistTracks = sortedTracks
                        )
                    )
                }
        }
    }
    fun checkAndDeleteTrackFromPlaylistTrackDatabase(track: TrackSearchModel) {
        viewModelScope.launch {
            playlistMediaDatabaseInteractor
                .getPlaylistsFromDatabase()
                .collect { listOfPlaylists ->
                    for (playlist in listOfPlaylists) {
                        val listsOfTrackIds = convertStringToList(playlist.listOfTracksId)
                        if (listsOfTrackIds.contains(track.trackId.toInt())) {
                            return@collect
                        }
                    }
                    deletePlaylistTrackFromDatabaseById(track.trackId.toInt())
                }
        }
    }
    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistDatabaseInteractor.insertPlaylistToDatabase(playlist)
        }
    }
    fun deletePlaylistTrackFromDatabaseById(id: Int) {
        viewModelScope.launch {
            playlistTrackDatabaseInteractor.deletePlaylistTrackFromDatabaseById(id)
        }
    }
    fun convertListToString(list: List<Int>): String {
        if (list.isEmpty()) return ""
        return list.joinToString(separator = ",")
    }
    fun convertStringToList(string: String): ArrayList<Int> {
        if (string.isEmpty()) return ArrayList<Int>()
        return ArrayList<Int>(string.split(",").map { item -> item.toInt() })
    }
    fun getMessageForExternalResources(playlist: Playlist?): String {
        var count = 0
        var message = ""
        val nameOfPlaylist = playlist?.name ?: ""
        val description =
            if (playlist?.description.isNullOrEmpty()) "without description" else playlist?.description
        val amountOfTracks = playlist?.amountOfTracks?.let { pluralizeWord(it, "трек") } ?: ""
        message += nameOfPlaylist + "\n" + description + "\n" + amountOfTracks + "\n"
        listOfCurrentTracks.forEach { track ->
            count++
            val formattedTime =
                track.trackTimeMillis?.split(":")
            val trackString = "$count ${track.artistName} - ${track.trackName} ($formattedTime) \n"
            message += trackString
        }
        return message
    }
    fun deletePlaylist(playlist: Playlist?) {
        if (playlist != null) {
            viewModelScope.launch {
                playlistMediaDatabaseInteractor.deletePlaylist(playlist)
            }
        }
    }
    fun pluralizeWord(number: Int, word: String): String {
        return when {
            number % 10 == 1 && number % 100 != 11 -> "$number $word"
            number % 10 in 2..4 && (number % 100 < 10 || number % 100 >= 20) ->
                if (word.endsWith('а')) "$number ${word.dropLast(1)}ы" else "$number ${word}а"
            else ->
                if (word.endsWith('а')) "$number ${word.dropLast(1)}" else "$number ${word}ов"
        }
    }
}