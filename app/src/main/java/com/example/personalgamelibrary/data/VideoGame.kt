package com.example.personalgamelibrary.data

enum class GameStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

data class VideoGame(
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