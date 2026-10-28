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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Critérios de ordenação da lista.
 *
 * :param labelRes: recurso de texto mostrado ao utilizador
 * :param defaultAscending: direção usada quando o critério é selecionado pela primeira vez
 */
enum class SortOption(@StringRes val labelRes: Int, val defaultAscending: Boolean) {
    DATE_ADDED(R.string.sort_date_added, defaultAscending = false), // mais recentes primeiro
    NAME(R.string.sort_name, defaultAscending = true), // A-Z
    YEAR(R.string.sort_year, defaultAscending = false) // mais recentes primeiro
}

/**
 * Ordenação ativa: critério e direção.
 *
 * :param option: critério de ordenação
 * :param ascending: true para crescente, false para decrescente
 */
data class SortOrder(
    val option: SortOption = SortOption.DATE_ADDED,
    val ascending: Boolean = option.defaultAscending
)

/**
 * Filtros ativos da lista. Um grupo vazio não restringe a lista; dentro de um grupo
 * basta o jogo corresponder a uma das opções (ou), e tem de cumprir todos os grupos (e).
 *
 * :param statuses: estados escolhidos
 * :param platforms: plataformas escolhidas
 * :param genres: géneros escolhidos
 */
data class GameFilters(
    val statuses: Set<GameStatus> = emptySet(),
    val platforms: Set<String> = emptySet(),
    val genres: Set<String> = emptySet()
) {
    /** Número total de opções marcadas nos três grupos. */
    val activeCount: Int get() = statuses.size + platforms.size + genres.size
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

    private val _sortOrder = MutableStateFlow(SortOrder())

    /** Ordenação ativa (critério e direção). */
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)

    /** Indica se a lista mostra apenas os jogos favoritos. */
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _filters = MutableStateFlow(GameFilters())

    /** Filtros de estado, plataforma e género ativos. */
    val filters: StateFlow<GameFilters> = _filters.asStateFlow()

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
     * favoritos, os filtros e a ordenação aplicados. Recalcula quando qualquer um dos quatro muda.
     *
     * O `WhileSubscribed(5000)` mantém a query ativa enquanto o ecrã a observa e
     * pára 5 segundos depois de deixar de ser observada. O intervalo evita
     * reiniciar tudo numa rotação do ecrã.
     */
    val games: StateFlow<List<VideoGame>> =
        combine(searchedGames, _sortOrder, _onlyFavorites, _filters)
        { list, order, favoritesOnly, filters ->
            val filtered = list.filter { game ->
                (!favoritesOnly || game.isFavorite) &&
                        (filters.statuses.isEmpty() || game.status in filters.statuses) &&
                        (filters.platforms.isEmpty() || game.platform in filters.platforms) &&
                        (filters.genres.isEmpty() || game.genre in filters.genres)
            }
            val comparator: Comparator<VideoGame> = when (order.option) {
                SortOption.DATE_ADDED -> compareBy { it.dateAdded }
                SortOption.NAME -> compareBy { it.name.lowercase() }
                SortOption.YEAR -> compareBy { it.releaseYear }
            }
            filtered.sortedWith(if (order.ascending) comparator else comparator.reversed())
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

    /**
     * Trata o toque numa opção de ordenação: se já estava selecionada inverte a direção,
     * caso contrário seleciona-a com a sua direção por defeito.
     *
     * :param option: opção em que o utilizador tocou
     */
    fun onSortOptionClick(option: SortOption) {
        _sortOrder.update { current ->
            if (current.option == option) {
                current.copy(ascending = !current.ascending)
            } else {
                SortOrder(option)
            }
        }
    }

    fun onOnlyFavoritesChange(value: Boolean) {
        _onlyFavorites.value = value
    }

    /**
     * Marca ou desmarca um estado nos filtros.
     *
     * :param status: estado em que o utilizador tocou
     */
    fun onStatusFilterToggle(status: GameStatus) {
        _filters.update { it.copy(statuses = it.statuses.toggled(status)) }
    }

    /**
     * Marca ou desmarca uma plataforma nos filtros.
     *
     * :param platform: plataforma em que o utilizador tocou
     */
    fun onPlatformFilterToggle(platform: String) {
        _filters.update { it.copy(platforms = it.platforms.toggled(platform)) }
    }

    /**
     * Marca ou desmarca um género nos filtros.
     *
     * :param genre: género em que o utilizador tocou
     */
    fun onGenreFilterToggle(genre: String) {
        _filters.update { it.copy(genres = it.genres.toggled(genre)) }
    }

    /** Remove todos os filtros de estado, plataforma e género (não mexe nos favoritos). */
    fun onClearFilters() {
        _filters.value = GameFilters()
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

/** Devolve um novo conjunto sem o item, se já lá estava, ou com ele, se não estava. */
private fun <T> Set<T>.toggled(item: T): Set<T> = if (item in this) this - item else this + item