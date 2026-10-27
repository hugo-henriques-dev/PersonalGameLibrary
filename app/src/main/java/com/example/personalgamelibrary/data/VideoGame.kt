package com.example.personalgamelibrary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GameStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

@Entity(tableName = "video_games")
data class VideoGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val platform: String,
    val genre: String,
    val releaseYear: Int,
    val status: GameStatus = GameStatus.NOT_STARTED,
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val coverImage: String? = null
)
