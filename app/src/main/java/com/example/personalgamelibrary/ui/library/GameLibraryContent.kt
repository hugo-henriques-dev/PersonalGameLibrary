package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.ui.navigation.Routes

/**
 * Define a navegação entre a lista e o formulário e mostra o diálogo de
 * confirmação ao eliminar um jogo.
 *
 * O formulário recebe o id do jogo na rota: sem id adiciona, com id edita
 * (o jogo é lido da base de dados).
 *
 * @param gameViewModel ViewModel que fornece o estado e executa as operações
 * @param modifier modificador de layout
 */
@Composable
fun GameLibraryContent(
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var gameToDelete by remember { mutableStateOf<VideoGame?>(null) }
    val platforms = stringArrayResource(R.array.platform_list).toList()
    val genres = stringArrayResource(R.array.genre_list).toList()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier
    ) {
        composable(Routes.LIST) {
            val games by gameViewModel.games.collectAsState()
            val searchQuery by gameViewModel.searchQuery.collectAsState()
            val stats by gameViewModel.stats.collectAsState()
            val sortOrder by gameViewModel.sortOrder.collectAsState()
            val filters by gameViewModel.filters.collectAsState()
            val onlyFavorites by gameViewModel.onlyFavorites.collectAsState()
            val darkOverride by gameViewModel.darkTheme.collectAsState()
            val isDarkTheme = darkOverride ?: isSystemInDarkTheme()

            GameListScreen(
                games = games,
                stats = stats,
                searchQuery = searchQuery,
                sortOrder = sortOrder,
                filters = filters,
                platforms = platforms,
                genres = genres,
                onlyFavorites = onlyFavorites,
                isDarkTheme = isDarkTheme,
                onSearchQueryChange = { gameViewModel.onSearchQueryChange(it) },
                onSortOptionClick = { gameViewModel.onSortOptionClick(it) },
                onStatusFilterToggle = { gameViewModel.onStatusFilterToggle(it) },
                onPlatformFilterToggle = { gameViewModel.onPlatformFilterToggle(it) },
                onGenreFilterToggle = { gameViewModel.onGenreFilterToggle(it) },
                onClearFilters = { gameViewModel.onClearFilters() },
                onOnlyFavoritesChange = { gameViewModel.onOnlyFavoritesChange(it) },
                onAddClick = { navController.navigate(Routes.form()) },
                onEditGame = { game -> navController.navigate(Routes.form(game.id)) },
                onDeleteGame = { game -> gameToDelete = game },
                onToggleFavorite = { game -> gameViewModel.toggleFavorite(game) },
                onToggleTheme = { gameViewModel.setDarkTheme(!isDarkTheme) }
            )
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(
                navArgument(Routes.GAME_ID_ARG) {
                    type = NavType.IntType
                    defaultValue = Routes.NO_GAME_ID
                }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt(Routes.GAME_ID_ARG)
                ?: Routes.NO_GAME_ID
            val isEditing = gameId != Routes.NO_GAME_ID

            // Ao editar, o jogo é lido da base de dados pelo id (e não da lista em memória)
            val gameToEdit by produceState<VideoGame?>(null, gameId) {
                if (isEditing) value = gameViewModel.getGameById(gameId)
            }

            // Só mostra o formulário quando o jogo a editar já foi carregado,
            // para nunca abrir um formulário vazio em modo "adicionar" por engano
            if (!isEditing || gameToEdit != null) {
                GameFormScreen(
                    platforms = platforms,
                    genres = genres,
                    gameToEdit = gameToEdit,
                    onSave = { game ->
                        if (isEditing) {
                            gameViewModel.updateGame(game)
                        } else {
                            gameViewModel.addGame(game)
                        }
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }

    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text(stringResource(R.string.dialog_delete_title)) },
            text = { Text(
                stringResource(
                    R.string.dialog_delete_message, game.name))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameViewModel.deleteGame(game)
                        gameToDelete = null
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
