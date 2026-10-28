package com.example.personalgamelibrary.ui.library

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.personalgamelibrary.GameLibraryApplication
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.ThemePreferences
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.VideoGameDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Critérios de ordenação da lista. */
enum class SortOption(@StringRes val labelRes: Int) {
    NEWEST(R.string.sort_newest),
    NAME(R.string.sort_name),
    YEAR(R.string.sort_year)
}

/** Contagens da coleção inteira, sem influência da pesquisa nem dos filtros. */
data class CollectionStats(
    val total: Int = 0,
    val inProgress: Int = 0,
    val completed: Int = 0
)

/**
 * ViewModel da coleção de videojogos.
 *
 * Expõe o estado do ecrã da lista como `StateFlow` (lista filtrada, estatísticas,
 * pesquisa, ordenação e filtro de favoritos) e encapsula as operações de escrita
 * na base de dados, que correm em corrotinas do `viewModelScope`.
 *
 * :param videoGameDao: acesso à tabela de videojogos
 * :param themePreferences: armazenamento da escolha de tema
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(
    private val videoGameDao: VideoGameDao,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    /** Texto atual da caixa de pesquisa. */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST)

    /** Critério de ordenação selecionado. */
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)

    /** Indica se a lista mostra apenas os jogos favoritos. */
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _darkTheme = MutableStateFlow(themePreferences.load())

    /** Tema escolhido pelo utilizador: true = escuro, false = claro, null = seguir o sistema. */
    val darkTheme: StateFlow<Boolean?> = _darkTheme.asStateFlow()

    /**
     * Jogos que correspondem à pesquisa atual.
     *
     * O `flatMapLatest` troca de query à base de dados sempre que o texto muda e
     * cancela a anterior, evitando resultados antigos a chegar depois dos novos.
     */
    private val searchedGames = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) videoGameDao.getAll() else videoGameDao.searchByName(query.trim())
    }

    /**
     * Lista final mostrada no ecrã: resultado da pesquisa, com o filtro de
     * favoritos e a ordenação aplicados. Recalcula quando qualquer um dos três muda.
     *
     * O `WhileSubscribed(5000)` mantém a query ativa enquanto o ecrã a observa e
     * pára 5 segundos depois de deixar de ser observada. O intervalo evita
     * reiniciar tudo numa rotação do ecrã.
     */
    val games: StateFlow<List<VideoGame>> =
        combine(searchedGames, _sortOption, _onlyFavorites) { list, sort, favoritesOnly ->
            val filtered = if (favoritesOnly) list.filter { it.isFavorite } else list
            when (sort) {
                SortOption.NEWEST -> filtered // o DAO já ordena por data, do mais recente
                SortOption.NAME -> filtered.sortedBy { it.name.lowercase() }
                SortOption.YEAR -> filtered.sortedByDescending { it.releaseYear }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Estatísticas da coleção inteira. Lê diretamente de `getAll()` e não de
     * [games], por isso a pesquisa e os filtros não alteram os totais.
     */
    val stats: StateFlow<CollectionStats> = videoGameDao.getAll()
        .map { all ->
            CollectionStats(
                total = all.size,
                inProgress = all.count { it.status == GameStatus.IN_PROGRESS },
                completed = all.count { it.status == GameStatus.COMPLETED }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CollectionStats()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSortOptionChange(option: SortOption) {
        _sortOption.value = option
    }

    fun onOnlyFavoritesChange(value: Boolean) {
        _onlyFavorites.value = value
    }

    /**
     * Define e guarda o tema escolhido pelo utilizador.
     *
     * :param enabled: true para o modo escuro, false para o claro
     */
    fun setDarkTheme(enabled: Boolean) {
        _darkTheme.value = enabled
        themePreferences.save(enabled)
    }

    fun addGame(game: VideoGame) {
        viewModelScope.launch { videoGameDao.insert(game) }
    }

    fun updateGame(game: VideoGame) {
        viewModelScope.launch { videoGameDao.update(game) }
    }

    fun deleteGame(game: VideoGame) {
        viewModelScope.launch { videoGameDao.delete(game) }
    }

    fun toggleFavorite(game: VideoGame) {
        viewModelScope.launch { videoGameDao.update(game.copy(isFavorite = !game.isFavorite)) }
    }

    suspend fun getGameById(id: Int): VideoGame? = videoGameDao.getById(id)

    /**
     * Cria o [GameViewModel] com a dependência (o DAO) vinda da [GameLibraryApplication],
     * já que o construtor tem parâmetros e o sistema não o sabe instanciar sozinho.
     *
     * :param application: aplicação que guarda a instância única da base de dados
     */
    class Factory(private val application: GameLibraryApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(
                    application.database.videoGameDao(),
                    ThemePreferences(application)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}