package com.example.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.db.dao.PlaylistTrackDao
import com.example.playlistmaker.library.data.db.entity.PlaylistTrackEntity

@Database(version = 1, entities = [PlaylistTrackEntity::class])
abstract class PlaylistTrackDatabase : RoomDatabase() {

    abstract fun playlistTrackDao(): PlaylistTrackDao

}