package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    sortOption: SortOption,
    onlyFavorites: Boolean,
    isDarkTheme: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
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
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = onlyFavorites,
                    onClick = { onOnlyFavoritesChange(!onlyFavorites) },
                    label = { Text(stringResource(R.string.filter_favorites)) }
                )
                SortMenu(selected = sortOption, onSelected = onSortOptionChange)
            }

            if (games.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(
                            if (searchQuery.isBlank() && !onlyFavorites) {
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

/** Botão que abre um menu com os critérios de ordenação. */
@Composable
private fun SortMenu(
    selected: SortOption,
    onSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                stringResource(
                    R.string.sort_label,
                    stringResource(selected.labelRes)
                )
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.labelRes)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
