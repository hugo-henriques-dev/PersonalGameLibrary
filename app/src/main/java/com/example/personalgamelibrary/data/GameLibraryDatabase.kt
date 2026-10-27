package com.example.personalgamelibrary.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [VideoGame::class], version = 1)
abstract class GameLibraryDatabase : RoomDatabase() {
    abstract fun videoGameDao(): VideoGameDao
}