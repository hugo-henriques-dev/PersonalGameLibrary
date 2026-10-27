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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.genreList
import com.example.personalgamelibrary.data.platformList
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme
import java.util.Calendar

private const val MIN_RELEASE_YEAR = 1950
private const val RELEASE_YEAR_LENGTH = 4

/**
 * Ecrã 2: Formulário para Adicionar ou Editar um Videojogo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFormScreen(
    platforms: List<String>,
    genres: List<String>,
    onSave: (VideoGame) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    gameToEdit: VideoGame? = null
) {
    var nameInput by rememberSaveable { mutableStateOf(gameToEdit?.name ?: "") }
    var selectedPlatform by rememberSaveable { mutableStateOf(gameToEdit?.platform ?: "") }
    var selectedGenre by rememberSaveable { mutableStateOf(gameToEdit?.genre ?: "") }
    var releaseYearInput by rememberSaveable {
        mutableStateOf(
            gameToEdit?.releaseYear?.toString() ?: ""
        )
    }
    var selectedStatus by rememberSaveable {
        mutableStateOf(gameToEdit?.status ?: GameStatus.NOT_STARTED)
    }
    var isFavoriteChecked by rememberSaveable {
        mutableStateOf(gameToEdit?.isFavorite ?: false)
    }
    var coverImageInput by rememberSaveable { mutableStateOf(gameToEdit?.coverImage ?: "") }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val releaseYear = releaseYearInput.toIntOrNull()
    val isYearError = releaseYearInput.isNotEmpty() &&
            (releaseYear == null || releaseYear !in MIN_RELEASE_YEAR..currentYear)
    val isFormValid = nameInput.isNotBlank() &&
            selectedPlatform.isNotEmpty() &&
            selectedGenre.isNotEmpty() &&
            releaseYear != null &&
            !isYearError

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (gameToEdit == null) "Adicionar Videojogo" else "Editar Videojogo",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagem e URL da Capa (Com Pré-Visualização Visual em Tempo Real)
            Text(
                "Capa do Videojogo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(90.dp, 120.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (coverImageInput.isNotBlank()) {
                        AsyncImage(
                            model = coverImageInput,
                            contentDescription = "Pré-visualização da Capa",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "🖼️\nSem Capa",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = coverImageInput,
                        onValueChange = { coverImageInput = it },
                        label = { Text("URL da Imagem da Capa") },
                        placeholder = { Text("https://images.igdb.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Nome do Jogo *") },
                placeholder = { Text("Ex: The Legend of Zelda") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = "Plataforma *",
                placeholder = "Escolha uma plataforma",
                options = platforms,
                selected = selectedPlatform,
                onSelected = { selectedPlatform = it },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Género *",
                placeholder = "Escolha um género",
                options = genres,
                selected = selectedGenre,
                onSelected = { selectedGenre = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = releaseYearInput,
                onValueChange = { newValue ->
                    if (newValue.length <= RELEASE_YEAR_LENGTH && newValue.all { it.isDigit() }) {
                        releaseYearInput = newValue
                    }
                },
                label = { Text("Ano de Lançamento *") },
                placeholder = { Text("Ex: 2023") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isYearError,
                supportingText = {
                    if (isYearError) Text("Insira um ano entre $MIN_RELEASE_YEAR e $currentYear")
                },
                modifier = Modifier.fillMaxWidth()
            )

            StatusSelector(
                selected = selectedStatus,
                onSelected = { selectedStatus = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Marcar como Favorito", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isFavoriteChecked,
                    onCheckedChange = { isFavoriteChecked = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val validYear = releaseYear ?: Calendar.getInstance().get(Calendar.YEAR)
                        onSave(
                            VideoGame(
                                id = gameToEdit?.id ?: 0,
                                name = nameInput.trim(),
                                platform = selectedPlatform,
                                genre = selectedGenre,
                                releaseYear = validYear,
                                status = selectedStatus,
                                dateAdded = gameToEdit?.dateAdded ?: System.currentTimeMillis(),
                                isFavorite = isFavoriteChecked,
                                coverImage = coverImageInput.ifBlank { null }
                            )
                        )
                    },
                    enabled = isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (gameToEdit == null) "Guardar" else "Atualizar")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/**
 * Menu suspenso personalizado para selecionar opções.
 */
@Composable
private fun DropdownField(
    label: String,
    placeholder: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selected.ifEmpty { placeholder })
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/** Seleção de Estado através de RadioButtons. */
@Composable
private fun StatusSelector(
    selected: GameStatus,
    onSelected: (GameStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Estado do Jogo", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        GameStatus.entries.forEach { status ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                RadioButton(
                    selected = status == selected,
                    onClick = { onSelected(status) }
                )
                Text(statusLabel(status))
            }
        }
    }
}

private fun statusLabel(status: GameStatus): String = when (status) {
    GameStatus.NOT_STARTED -> "Por Jogar"
    GameStatus.IN_PROGRESS -> "A Jogar"
    GameStatus.COMPLETED -> "Concluído"
}

@Preview(showBackground = true)
@Composable
private fun GameFormScreenPreview() {
    PersonalGameLibraryTheme {
        GameFormScreen(
            platforms = platformList,
            genres = genreList,
            onSave = {},
            onCancel = {}
        )
    }
}
