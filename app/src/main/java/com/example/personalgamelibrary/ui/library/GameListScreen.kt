package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import com.example.personalgamelibrary.data.GameStatus
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.ui.components.clearFocusOnTapOrScroll

/**
 * Ecrã da lista de videojogos, com pesquisa, filtro de favoritos, ordenação e estatísticas.
 *
 * Não guarda estado próprio: recebe-o por parâmetro e comunica as ações do utilizador
 * através de callbacks, ficando o estado no [GameViewModel].
 *
 * :param games: jogos a mostrar, já pesquisados, filtrados e ordenados
 * :param stats: totais da coleção inteira, sem influência da pesquisa nem dos filtros
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    games: List<VideoGame>,
    stats: CollectionStats,
    searchQuery: String,
    sortOrder: SortOrder,
    filters: GameFilters,
    platforms: List<String>,
    genres: List<String>,
    onlyFavorites: Boolean,
    isDarkTheme: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionClick: (SortOption) -> Unit,
    onStatusFilterToggle: (GameStatus) -> Unit,
    onPlatformFilterToggle: (String) -> Unit,
    onGenreFilterToggle: (String) -> Unit,
    onClearFilters: () -> Unit,
    onOnlyFavoritesChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onEditGame: (VideoGame) -> Unit,
    onDeleteGame: (VideoGame) -> Unit,
    onToggleFavorite: (VideoGame) -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.list_title), fontWeight = FontWeight.Bold)
                        Text(
                            text = stringResource(
                                R.string.list_stats,
                                stats.total,
                                stats.inProgress,
                                stats.completed
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) {
                                Icons.Default.LightMode
                            } else {
                                Icons.Default.DarkMode
                            },
                            contentDescription = stringResource(
                                if (isDarkTheme) {
                                    R.string.theme_switch_to_light
                                } else {
                                    R.string.theme_switch_to_dark
                                }
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.list_add)
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clearFocusOnTapOrScroll()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(stringResource(R.string.search_label)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon_description)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.search_clear)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = onlyFavorites,
                    onClick = { onOnlyFavoritesChange(!onlyFavorites) },
                    label = { Text(stringResource(R.string.filter_favorites)) }
                )
                FilterSelector(
                    filters = filters,
                    platforms = platforms,
                    genres = genres,
                    onStatusToggle = onStatusFilterToggle,
                    onPlatformToggle = onPlatformFilterToggle,
                    onGenreToggle = onGenreFilterToggle,
                    onClear = onClearFilters
                )
                SortSelector(order = sortOrder, onOptionClick = onSortOptionClick)
            }

            if (games.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(
                            if (searchQuery.isBlank() &&
                                !onlyFavorites && filters.activeCount == 0) {
                                R.string.list_empty
                            } else {
                                R.string.list_no_results
                            }
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games, key = { it.id }) { game ->
                        GameItemCard(
                            game = game,
                            onEdit = { onEditGame(game) },
                            onDelete = { onDeleteGame(game) },
                            onToggleFavorite = { onToggleFavorite(game) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Botão que mostra a ordenação ativa (nome e seta) e abre um painel inferior para a alterar.
 *
 * Tocar numa opção já selecionada inverte a direção; tocar noutra seleciona-a com a sua
 * direção por defeito. O painel fica aberto para se ver o resultado e fecha ao arrastar
 * para baixo, ao tocar fora ou com o botão voltar.
 *
 * :param order: ordenação ativa
 * :param onOptionClick: chamado quando o utilizador toca numa opção do painel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortSelector(
    order: SortOrder,
    onOptionClick: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showSheet = true }, modifier = modifier) {
        Text(stringResource(order.option.labelRes))
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        SortDirectionIcon(
            ascending = order.ascending,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Text(
                text = stringResource(R.string.sort_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            SortOption.entries.forEach { option ->
                ListItem(
                    headlineContent = { Text(stringResource(option.labelRes)) },
                    trailingContent = {
                        if (option == order.option) {
                            SortDirectionIcon(ascending = order.ascending)
                        }
                    },
                    // O ListItem tem fundo próprio; transparente para herdar o do painel
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable { onOptionClick(option) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Seta para cima (ordem crescente) ou para baixo (ordem decrescente). */
@Composable
private fun SortDirectionIcon(ascending: Boolean, modifier: Modifier = Modifier) {
    Icon(
        imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
        contentDescription = stringResource(
            if (ascending) R.string.sort_ascending else R.string.sort_descending
        ),
        modifier = modifier
    )
}

/**
 * Botão que abre um painel inferior com os filtros de estado, plataforma e género.
 *
 * Dentro de cada grupo basta corresponder a uma das opções marcadas; entre grupos tem de
 * cumprir todos. O botão mostra quantas opções estão marcadas.
 *
 * :param filters: filtros ativos
 * :param platforms: plataformas disponíveis para escolher
 * :param genres: géneros disponíveis para escolher
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterSelector(
    filters: GameFilters,
    platforms: List<String>,
    genres: List<String>,
    onStatusToggle: (GameStatus) -> Unit,
    onPlatformToggle: (String) -> Unit,
    onGenreToggle: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showSheet = true }, modifier = modifier) {
        Text(
            if (filters.activeCount == 0) {
                stringResource(R.string.filter_title)
            } else {
                stringResource(R.string.filter_title_active, filters.activeCount)
            }
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.filter_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClear, enabled = filters.activeCount > 0) {
                        Text(stringResource(R.string.filter_clear))
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_status)) {
                    GameStatus.entries.forEach { status ->
                        FilterChip(
                            selected = status in filters.statuses,
                            onClick = { onStatusToggle(status) },
                            label = { Text(stringResource(status.labelRes)) }
                        )
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_platform)) {
                    platforms.forEach { platform ->
                        FilterChip(
                            selected = platform in filters.platforms,
                            onClick = { onPlatformToggle(platform) },
                            label = { Text(platform) }
                        )
                    }
                }

                FilterSection(title = stringResource(R.string.filter_section_genre)) {
                    genres.forEach { genre ->
                        FilterChip(
                            selected = genre in filters.genres,
                            onClick = { onGenreToggle(genre) },
                            label = { Text(genre) }
                        )
                    }
                }
            }
        }
    }
}

/** Grupo de chips de um filtro, com título; os chips passam para a linha seguinte se não couberem. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}
