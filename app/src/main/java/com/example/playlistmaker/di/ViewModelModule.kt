package com.example.playlistmaker.di
import com.example.playlistmaker.library.ui.viewModel.FavoriteViewModel
import com.example.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.player.ui.viewModel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewModel.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get(), get())
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
        PlaylistViewModel()
    }
}