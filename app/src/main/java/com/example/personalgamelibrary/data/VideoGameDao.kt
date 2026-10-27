package com.example.personalgamelibrary.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoGameDao {
    @Insert
    suspend fun insert(game: VideoGame)

    @Query("SELECT * FROM video_games ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<VideoGame>>
}