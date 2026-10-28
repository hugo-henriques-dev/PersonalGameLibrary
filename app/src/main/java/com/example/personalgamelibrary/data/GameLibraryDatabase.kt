package com.example.personalgamelibrary.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de dados local (Room/SQLite) da aplicação.
 *
 * Se a estrutura de [VideoGame] mudar, é preciso aumentar o `version` e fornecer uma
 * migração, caso contrário o Room recusa abrir a base de dados já existente.
 */
@Database(entities = [VideoGame::class], version = 1, exportSchema = false)
abstract class GameLibraryDatabase : RoomDatabase() {
    abstract fun videoGameDao(): VideoGameDao
}
