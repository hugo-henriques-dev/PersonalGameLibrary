package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.genreList
import com.example.personalgamelibrary.data.platformList

/**
 * Gestor de Navegação entre Ecrãs da Aplicação.
 */
@Composable
fun GameLibraryContent(
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val games by gameViewModel.games.collectAsState()
    val searchQuery by gameViewModel.searchQuery.collectAsState()

    var isFormShown by remember { mutableStateOf(false) }
    var gameToEdit by remember { mutableStateOf<VideoGame?>(null) }
    var gameToDelete by remember { mutableStateOf<VideoGame?>(null) }

    if (isFormShown) {
        // Ecrã 2: Formulário de Adição/Edição
        GameFormScreen(
            platforms = platformList,
            genres = genreList,
            gameToEdit = gameToEdit,
            onSave = { game ->
                if (gameToEdit == null) {
                    gameViewModel.addGame(game)
                } else {
                    gameViewModel.updateGame(game)
                }
                isFormShown = false
                gameToEdit = null
            },
            onCancel = {
                isFormShown = false
                gameToEdit = null
            },
            modifier = modifier
        )
    } else {
        // Ecrã 1: Lista de Jogos
        GameListScreen(
            games = games,
            searchQuery = searchQuery,
            onSearchQueryChange = { gameViewModel.onSearchQueryChange(it) },
            onAddClick = {
                gameToEdit = null
                isFormShown = true
            },
            onEditGame = { game ->
                gameToEdit = game
                isFormShown = true
            },
            onDeleteGame = { game ->
                gameToDelete = game
            },
            modifier = modifier
        )
    }

    // Diálogo de confirmação para eliminar
    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("Eliminar Videojogo") },
            text = { Text("Tem a certeza de que deseja eliminar \"${game.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameViewModel.deleteGame(game)
                        gameToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Ecrã 1: Lista de Videojogos Registados com Capas, Pesquisa e Estatísticas Rápidas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    games: List<VideoGame>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditGame: (VideoGame) -> Unit,
    onDeleteGame: (VideoGame) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalGames = games.size
    val completedGames = games.count { it.status == GameStatus.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("A Minha Coleção", fontWeight = FontWeight.Bold)
                        Text(
                            text = "$totalGames jogos registados • $completedGames concluídos",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar"
                )
            }
        },

        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Barra de Pesquisa por Nome
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Pesquisar jogo por nome...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpar pesquisa"
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            if (games.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank())
                            "Nenhum jogo registado.\nClique no botão + para adicionar!"
                        else
                            "Nenhum jogo encontrado para \"$searchQuery\".",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(games) { game ->
                        GameItemCard(
                            game = game,
                            onEdit = { onEditGame(game) },
                            onDelete = { onDeleteGame(game) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cartão Visual do Videojogo com Imagem de Capa Realista, Chips e Opções.
 */
@Composable
fun GameItemCard(
    game: VideoGame,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagem Visual da Capa do Jogo (Utilizando Coil AsyncImage)
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(85.dp, 115.dp)
            ) {
                if (!game.coverImage.isNullOrBlank()) {
                    AsyncImage(
                        model = game.coverImage,
                        contentDescription = "Capa de ${game.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎮", style = MaterialTheme.typography.headlineLarge)
                    }
                }
            }

            // Detalhes e Ações
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = game.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3
                        )
                        if (game.isFavorite) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "★",
                                color = Color(0xFFFFB300),
                                style = MaterialTheme.typography.titleSmall)
                        }
                    }

                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = game.status)
                    AssistChip(
                        onClick = {},
                        label = { Text(game.platform, style = MaterialTheme.typography.labelSmall) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(game.genre, style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ano: ${game.releaseYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: GameStatus) {
    val (label, containerColor, contentColor) = when (status) {
        GameStatus.NOT_STARTED -> Triple("Por Jogar", Color(0xFFE0E0E0), Color(0xFF424242))
        GameStatus.IN_PROGRESS -> Triple("A Jogar", Color(0xFFBBDEFB), Color(0xFF0D47A1))
        GameStatus.COMPLETED -> Triple("Concluído", Color(0xFFC8E6C9), Color(0xFF1B5E20))
    }

    SuggestionChip(
        onClick = {},
        label = {
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall)
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        )
    )
}
