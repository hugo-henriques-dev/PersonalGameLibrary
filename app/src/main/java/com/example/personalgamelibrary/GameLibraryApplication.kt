package com.example.personalgamelibrary

import android.app.Application
import androidx.room.Room
import com.example.personalgamelibrary.data.GameLibraryDatabase

/**
 * Classe de aplicação: guarda a instância única da base de dados, partilhada por toda a app.
 */
class GameLibraryApplication : Application() {

    /** Criada apenas no primeiro acesso (`lazy`) e reutilizada daí em diante. */
    val database: GameLibraryDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            GameLibraryDatabase::class.java,
            "game_library.db"
        ).build()
    }
}
