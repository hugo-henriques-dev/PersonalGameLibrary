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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame

/**
 * Cartão de um jogo na lista: capa, nome, estado, plataforma, género e ano de lançamento,
 * com botões para favorito, edição e eliminação.
 *
 * Quando o jogo não tem capa, mostra um emoji no lugar da imagem.
 */
@Composable
fun GameItemCard(
    game: VideoGame,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
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
                        contentDescription = stringResource(
                            R.string.game_cover_description,
                            game.name
                        ),
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
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 3,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResource(
                                    if (game.isFavorite) {
                                        R.string.favorite_remove
                                    } else {
                                        R.string.favorite_add
                                    }
                                ),
                                tint = if (game.isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.game_edit)
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(status = game.status)
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                game.platform,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(game.genre, style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.game_year, game.releaseYear),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Chip com o estado do jogo, com cores diferentes para cada estado. */
@Composable
private fun StatusBadge(status: GameStatus) {
    val colors = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (status) {
        GameStatus.NOT_STARTED -> colors.surfaceVariant to colors.onSurfaceVariant
        GameStatus.IN_PROGRESS -> colors.primaryContainer to colors.onPrimaryContainer
        GameStatus.COMPLETED -> colors.tertiaryContainer to colors.onTertiaryContainer
    }

    SuggestionChip(
        onClick = {},
        label = {
            Text(
                stringResource(status.labelRes),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        )
    )
}
