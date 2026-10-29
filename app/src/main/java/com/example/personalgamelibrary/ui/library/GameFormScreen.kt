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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgamelibrary.R
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.ui.components.clearFocusOnTapOrScroll
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme
import java.util.Calendar

private const val MIN_RELEASE_YEAR = 1950
private const val RELEASE_YEAR_LENGTH = 4

/**
 * Formulário para adicionar um novo videojogo ou editar um existente.
 *
 * Valida se os campos obrigatórios (nome, plataforma, género e ano de lançamento)
 * foram preenchidos corretamente antes de permitir guardar. Quando [gameToEdit] é
 * fornecido, pré-preenche os campos para edição.
 *
 * @param platforms lista de plataformas disponíveis para escolha
 * @param genres lista de géneros disponíveis para escolha
 * @param onSave callback invocado com o [VideoGame] preenchido para guardar ou atualizar
 * @param onCancel callback invocado para sair do formulário sem guardar
 * @param modifier modificador de layout
 * @param gameToEdit jogo a editar, ou null para adicionar um jogo novo
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
                        text = stringResource(
                            if (gameToEdit == null) {
                                R.string.form_title_add
                            } else {
                                R.string.form_title_edit
                            }
                        ),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.form_back)
                        )
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
                .clearFocusOnTapOrScroll()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagem e URL da Capa (Com Pré-Visualização Visual em Tempo Real)
            Text(
                stringResource(R.string.form_cover_section),
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
                            contentDescription = stringResource(
                                R.string.form_cover_preview_description
                            ),
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
                                stringResource(R.string.form_cover_empty),
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
                        label = { Text(stringResource(R.string.form_cover_url_label)) },
                        placeholder = {
                            Text(stringResource(R.string.form_cover_url_placeholder))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text(stringResource(R.string.form_name_label)) },
                placeholder = { Text(stringResource(R.string.form_name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = stringResource(R.string.form_platform_label),
                placeholder = stringResource(R.string.form_platform_placeholder),
                options = platforms,
                selected = selectedPlatform,
                onSelected = { selectedPlatform = it },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = stringResource(R.string.form_genre_label),
                placeholder = stringResource(R.string.form_genre_placeholder),
                options = genres,
                selected = selectedGenre,
                onSelected = { selectedGenre = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = releaseYearInput,
                onValueChange = { newValue ->
                    if (newValue.length <= RELEASE_YEAR_LENGTH &&
                        newValue.all { it.isDigit() }
                    ) {
                        releaseYearInput = newValue
                    }
                },
                label = { Text(stringResource(R.string.form_year_label)) },
                placeholder = { Text(stringResource(R.string.form_year_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isYearError,
                supportingText = {
                    if (isYearError) {
                        Text(stringResource(
                            R.string.form_year_error,
                            MIN_RELEASE_YEAR,
                            currentYear
                        ))
                    }
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
                Text(
                    stringResource(R.string.form_favorite_label),
                    style = MaterialTheme.typography.bodyLarge
                )
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
                        val validYear = releaseYear
                            ?: Calendar.getInstance().get(Calendar.YEAR)
                        onSave(
                            VideoGame(
                                id = gameToEdit?.id ?: 0,
                                name = nameInput.trim(),
                                platform = selectedPlatform,
                                genre = selectedGenre,
                                releaseYear = validYear,
                                status = selectedStatus,
                                dateAdded = gameToEdit?.dateAdded
                                    ?: System.currentTimeMillis(),
                                isFavorite = isFavoriteChecked,
                                coverImage = coverImageInput.ifBlank { null }
                            )
                        )
                    },
                    enabled = isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(
                            if (gameToEdit == null) {
                                R.string.form_save
                            } else {
                                R.string.form_update
                            }
                        )
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    }
}

/**
 * Menu suspenso personalizado para selecionar opções.
 */
@OptIn(ExperimentalMaterial3Api::class)
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

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
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
        Text(
            stringResource(R.string.form_status_label),
            style = MaterialTheme.typography.bodyLarge
        )
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
                Text(stringResource(status.labelRes))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameFormScreenPreview() {
    PersonalGameLibraryTheme {
        GameFormScreen(
            platforms = stringArrayResource(R.array.platform_list).toList(),
            genres = stringArrayResource(R.array.genre_list).toList(),
            onSave = {},
            onCancel = {}
        )
    }
}
