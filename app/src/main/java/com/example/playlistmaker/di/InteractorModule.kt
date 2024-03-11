package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.library.domain.impl.FavoritesInteractorImpl

val interactorModule = module {
    factory<PlayerInteractor> {
        PlayerInteractorImpl(playerRepository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get(), sharingRepository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(sharingRepository = get())
    }

    factory<SearchInteractor> {
        SearchInteractorImpl(repository = get())
    }
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}