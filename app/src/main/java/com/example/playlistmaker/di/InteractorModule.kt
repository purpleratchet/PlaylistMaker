package com.example.playlistmaker.di
import com.example.playlistmaker.library.domain.api.CurrentPlaylistInteractor
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.library.domain.api.PlaylistDatabaseInteractor
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseInteractor
import com.example.playlistmaker.library.domain.impl.CurrentPlaylistInteractorImpl
import com.example.playlistmaker.library.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.library.domain.impl.PlaylistDatabaseInteractorImpl
import com.example.playlistmaker.library.domain.impl.PlaylistMediaDatabaseInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlaylistTrackDatabaseInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module
val interactorModule = module {
    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
    single<SettingsInteractor> {
        SettingsInteractorImpl(get(), get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
    single<PlaylistDatabaseInteractor> {
        PlaylistDatabaseInteractorImpl(playlistDatabaseRepository = get())
    }
    single<PlaylistMediaDatabaseInteractor> {
        PlaylistMediaDatabaseInteractorImpl(playlistMediaDatabaseRepository = get())
    }

    single<PlaylistTrackDatabaseInteractor> {
        PlaylistTrackDatabaseInteractorImpl(playlistTrackDatabaseRepository = get())
    }
    single<CurrentPlaylistInteractor> {
        CurrentPlaylistInteractorImpl(currentPlaylistRepository = get())
    }
}