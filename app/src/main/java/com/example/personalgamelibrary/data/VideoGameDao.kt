package com.example.personalgamelibrary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Acesso à tabela `video_games`. As consultas que devolvem `Flow` emitem
 * de novo a cada alteração.
 */
@Dao
interface VideoGameDao {

    @Insert
    suspend fun insert(game: VideoGame)

    @Update
    suspend fun update(game: VideoGame)

    @Delete
    suspend fun delete(game: VideoGame)

    @Query("SELECT * FROM video_games ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<VideoGame>>

    /**
     * Devolve os jogos cujo nome contém o texto indicado (sem distinguir maiúsculas
     * de minúsculas), do mais recente para o mais antigo.
     *
     * @param query texto a procurar no nome
     * @return fluxo que emite os resultados sempre que a tabela muda
     */
    @Query(
        "SELECT * FROM video_games " +
            "WHERE name LIKE '%' || :query || '%' " +
            "ORDER BY dateAdded DESC"
    )
    fun searchByName(query: String): Flow<List<VideoGame>>

    @Query("SELECT * FROM video_games WHERE id = :id")
    suspend fun getById(id: Int): VideoGame?
}