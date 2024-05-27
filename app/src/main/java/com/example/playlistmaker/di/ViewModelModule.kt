package com.example.playlistmaker.di
import com.example.playlistmaker.library.ui.viewmodel.FavoriteViewModel
import com.example.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.library.ui.viewmodel.PlaylistInfoViewModel
import com.example.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get(), get(),get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get(), get(), get())
    }
    viewModel {
        FavoriteViewModel(get(), get())
    }
    viewModel {
        PlaylistViewModel(playlistMediaDatabaseInteractor = get())
    }
    viewModel {
        NewPlaylistViewModel(playlistDatabaseInteractor = get())
    }
    viewModel {
        PlaylistInfoViewModel(
            currentPlaylistInteractor = get(),
            playlistDatabaseInteractor = get(),
            playlistTrackDatabaseInteractor = get(),
            playlistMediaDatabaseInteractor = get()
        )
    }
}