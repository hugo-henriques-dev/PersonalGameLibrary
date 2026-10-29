package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame

/**
 * Cartão de um jogo na lista.
 *
 * Exibe a imagem de capa (carregada via Coil), nome, estado, plataforma, género e
 * ano de lançamento, com botões para alternar favorito, editar ou eliminar.
 * As etiquetas de estado, plataforma e género são apresentadas num [FlowRow]
 * responsivo para evitar sobreposição ou ocultação de texto quando os nomes são longos.
 *
 * @param game jogo a apresentar
 * @param onEdit callback invocado ao clicar no botão de editar
 * @param onDelete callback invocado ao clicar no botão de eliminar
 * @param onToggleFavorite callback invocado ao clicar no ícone de favorito
 * @param modifier modificador de layout
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
            // Capa do jogo (Coil AsyncImage ou emoji de fallback)
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
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
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.game_edit),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Etiquetas de Estado, Plataforma e Género num FlowRow responsivo
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusBadge(status = game.status)
                    CategoryBadge(label = game.platform)
                    CategoryBadge(label = game.genre)
                }

                Text(
                    text = stringResource(R.string.game_year, game.releaseYear),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Chip/Etiqueta compacta para plataforma ou género. */
@Composable
private fun CategoryBadge(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/** Etiqueta compacta com o estado do jogo e cores distintas. */
@Composable
private fun StatusBadge(
    status: GameStatus,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (status) {
        GameStatus.NOT_STARTED -> colors.surfaceVariant to colors.onSurfaceVariant
        GameStatus.IN_PROGRESS -> colors.primaryContainer to colors.onPrimaryContainer
        GameStatus.COMPLETED -> colors.tertiaryContainer to colors.onTertiaryContainer
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Text(
            text = stringResource(status.labelRes),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
