package com.example.playlistmaker.di
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.library.data.impl.PlaylistDatabaseRepositoryImpl
import com.example.playlistmaker.library.data.impl.PlaylistMediaDatabaseRepositoryImpl
import com.example.playlistmaker.library.domain.api.FavoritesRepository
import com.example.playlistmaker.library.domain.api.PlaylistDatabaseRepository
import com.example.playlistmaker.library.domain.api.PlaylistMediaDatabaseRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.data.PlaylistTrackDatabaseRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.PlaylistTrackDatabaseRepository
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module
val repositoryModule = module {
    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    factory<SharingRepository> {
        SharingRepositoryImpl(get())
    }
    factory<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            searchDataStorage = get(),
            appDatabase = get(),
        )
    }
    factory {
        TrackDbConverter()
    }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
    single<PlaylistDatabaseRepository> {
        PlaylistDatabaseRepositoryImpl(playlistDatabase = get())
    }
    single<PlaylistMediaDatabaseRepository> {
        PlaylistMediaDatabaseRepositoryImpl(playlistDatabase = get())
    }

    single<PlaylistTrackDatabaseRepository> {
        PlaylistTrackDatabaseRepositoryImpl(playlistTrackDatabase = get())
    }
}