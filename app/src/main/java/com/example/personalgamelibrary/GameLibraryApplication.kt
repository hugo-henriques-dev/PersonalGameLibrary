package com.example.personalgamelibrary

import android.app.Application
import androidx.room.Room
import com.example.personalgamelibrary.data.GameLibraryDatabase

class GameLibraryApplication : Application() {

    val database: GameLibraryDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            GameLibraryDatabase::class.java,
            "game_library.db"
        ).build()
    }
}
