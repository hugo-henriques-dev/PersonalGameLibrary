package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.genreList
import com.example.personalgamelibrary.data.platformList
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme
import java.util.Calendar

private const val MIN_RELEASE_YEAR = 1950
private const val RELEASE_YEAR_LENGTH = 4

/**
 * Form to add or edit a game.
 *
 * @param platforms options shown in the platform menu
 * @param genres options shown in the genre menu
 * @param onSave called when the user clicks submit
 * @param onCancel called when the user clicks cancel
 * @param modifier modifier applied to the root layout
 */
@Composable
fun GameFormScreen(
    platforms: List<String>,
    genres: List<String>,
    onSave: (VideoGame) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by rememberSaveable { mutableStateOf("") }
    var selectedPlatform by rememberSaveable { mutableStateOf("") }
    var selectedGenre by rememberSaveable { mutableStateOf("") }
    var releaseYearInput by rememberSaveable { mutableStateOf("") }
    var selectedStatus by rememberSaveable { mutableStateOf(GameStatus.NOT_STARTED) }
    var isFavoriteChecked by rememberSaveable { mutableStateOf(false) }

    // Derived values: recalculated on every redraw, so they are not stored as state.
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val releaseYear = releaseYearInput.toIntOrNull()
    val isYearError = releaseYearInput.isNotEmpty() &&
            (releaseYear == null || releaseYear !in MIN_RELEASE_YEAR..currentYear)
    val isFormValid = nameInput.isNotBlank() &&
            selectedPlatform.isNotEmpty() &&
            selectedGenre.isNotEmpty() &&
            releaseYear != null &&
            !isYearError

    Column(modifier = modifier) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Name") },
            placeholder = { Text("Type the game's name...") }
        )

        DropdownField(
            placeholder = "Choose a platform",
            options = platforms,
            selected = selectedPlatform,
            onSelected = { selectedPlatform = it }
        )

        DropdownField(
            placeholder = "Choose a genre",
            options = genres,
            selected = selectedGenre,
            onSelected = { selectedGenre = it }
        )

        OutlinedTextField(
            value = releaseYearInput,
            onValueChange = { newValue ->
                // The numeric keyboard only suggests digits; it does not enforce them
                // (pasting and some keyboards allow symbols), so filter here too.
                if (newValue.length <= RELEASE_YEAR_LENGTH && newValue.all { it.isDigit() }) {
                    releaseYearInput = newValue
                }
            },
            label = { Text("Release Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = isYearError,
            supportingText = {
                if (isYearError) Text("Enter a year between $MIN_RELEASE_YEAR and $currentYear")
            }
        )

        StatusSelector(
            selected = selectedStatus,
            onSelected = { selectedStatus = it }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Favorite")
            Switch(
                checked = isFavoriteChecked,
                onCheckedChange = { isFavoriteChecked = it }
            )
        }

        Row {
            OutlinedButton(
                onClick = {
                    onSave(
                        VideoGame(
                            name = nameInput,
                            platform = selectedPlatform,
                            genre = selectedGenre,
                            releaseYear = releaseYear!!,
                            status = selectedStatus,
                            isFavorite = isFavoriteChecked
                        )
                    )
                },
                enabled = isFormValid
            ) {
                Text("Submit")
            }

            OutlinedButton(
                onClick = onCancel
            ) {
                Text("Cancel")
            }
        }
    }
}

/**
 * Button that opens a menu of options and reports the one chosen.
 *
 * Whether the menu is open is internal state; the chosen option lives in the caller.
 */
@Composable
private fun DropdownField(
    placeholder: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }) {
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

/** Radio buttons for the three game statuses. */
@Composable
private fun StatusSelector(
    selected: GameStatus,
    onSelected: (GameStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Status")
        GameStatus.entries.forEach { status ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = status == selected,
                    onClick = { onSelected(status) }
                )
                Text(statusLabel(status))
            }
        }
    }
}

/** Text shown to the user for game status. */
private fun statusLabel(status: GameStatus): String = when (status) {
    GameStatus.NOT_STARTED -> "Not started"
    GameStatus.IN_PROGRESS -> "In progress"
    GameStatus.COMPLETED -> "Completed"
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