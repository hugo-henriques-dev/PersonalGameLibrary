package com.example.personalgamelibrary.data

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalgamelibrary.R

/**
 * Estado de um jogo na coleção do utilizador.
 *
 * O Room guarda o enum pelo nome da constante (por exemplo `IN_PROGRESS`), por isso
 * renomear uma constante deixa de reconhecer os dados já guardados.
 *
 * @param labelRes recurso de texto mostrado ao utilizador
 */
enum class GameStatus(@StringRes val labelRes: Int) {
    NOT_STARTED(R.string.status_not_started),
    IN_PROGRESS(R.string.status_in_progress),
    COMPLETED(R.string.status_completed)
}

/**
 * Videojogo da coleção. Cada instância corresponde a uma linha da tabela `video_games`.
 *
 * @param id identificador único, gerado pela base de dados (0 = ainda não inserido)
 * @param name nome do jogo
 * @param platform plataforma em que se joga
 * @param genre género do jogo
 * @param releaseYear ano de lançamento
 * @param status estado do jogo na coleção
 * @param dateAdded momento em que foi adicionado, em milissegundos desde 1970
 *   (usado na ordenação)
 * @param isFavorite true se estiver marcado como favorito
 * @param coverImage URL da imagem de capa, ou null se não tiver
 */
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