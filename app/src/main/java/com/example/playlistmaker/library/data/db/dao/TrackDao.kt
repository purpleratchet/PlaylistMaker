package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavorites(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Delete(entity = TrackEntity::class)
    suspend fun deleteFromFavorites(track: TrackEntity)

    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksID(): List<Long>
}