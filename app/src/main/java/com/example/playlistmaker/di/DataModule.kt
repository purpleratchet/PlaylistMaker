package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.SearchDataStorage
import com.example.playlistmaker.search.data.network.ITunesSearchApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.sharedPrefs.SharedPrefsSearchDataStorage
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import com.example.playlistmaker.utils.SHARED_PREFERENCES
import com.example.playlistmaker.utils.iTunesBaseUrl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext

import androidx.room.Room
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.PlaylistDatabase
import com.example.playlistmaker.library.data.db.PlaylistTrackDatabase
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    factory {
        MediaPlayer()
    }

    single {
        androidContext().getSharedPreferences(
            SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<ITunesSearchApi> {
        Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(iTunesService = get(), context = androidContext())
    }

    single<SearchDataStorage> {
        SharedPrefsSearchDataStorage(sharedPref = get(), gson = get())
    }

    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single<PlaylistDatabase> {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "playlist_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single<PlaylistTrackDatabase> {
        Room.databaseBuilder(androidContext(), PlaylistTrackDatabase::class.java, "playlist_track_databases.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}