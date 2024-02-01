package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.viewmodel.FavoriteViewModel
import com.example.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (trackUrl: String) ->
        PlayerViewModel(
            playerInteractor = get(),
            trackUrl = trackUrl
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchInteractor = get()
        )
    }

    viewModel {
        FavoriteViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}