package com.example.personalgamelibrary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoGameDao {
    @Insert
    fun insert(game: VideoGame)

    @Update
    fun update(game: VideoGame)

    @Delete
    fun delete(game: VideoGame)

    @Query("SELECT * FROM video_games ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<VideoGame>>

    @Query("SELECT * FROM video_games WHERE name LIKE '%' || :query || '%' ORDER BY dateAdded DESC")
    fun searchByName(query: String): Flow<List<VideoGame>>
}
