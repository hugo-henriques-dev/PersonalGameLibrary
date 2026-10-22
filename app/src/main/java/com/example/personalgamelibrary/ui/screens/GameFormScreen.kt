package com.example.personalgamelibrary.ui.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.data.genreList
import com.example.personalgamelibrary.data.platformList
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme
import java.util.Calendar

@Composable
fun GameFormScreen(
    platforms: List<String>,
    genres: List<String>
) {
    var nameInput by remember { mutableStateOf("") }

    // Platform
    var expandedPlatform by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("") }

    // Genre
    var expandedGenre by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("") }

    // Year
    var releaseYearInput by remember { mutableStateOf("") }
        // Year validation
    var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var releaseYear = releaseYearInput.toIntOrNull()
    var isYearError = releaseYearInput.isNotEmpty() &&
            (releaseYear == null || releaseYear !in 1950..currentYear)


    var selectedStatus by remember { mutableStateOf(GameStatus.NOT_STARTED) }
    var isFavoriteChecked by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Name") },
            placeholder = { Text("Type the game's name...") }
        )

        Box {
            OutlinedButton(onClick = { expandedPlatform = true }) {
                Text(
                    if (selectedPlatform.isEmpty())
                        "Choose a platform"
                    else
                        selectedPlatform
                )
            }

            DropdownMenu(
                expanded = expandedPlatform,
                onDismissRequest = { expandedPlatform = false}
            ) {
                platforms.forEach { platform ->
                    DropdownMenuItem(
                        text = { Text(platform) },
                        onClick = {
                            selectedPlatform = platform
                            expandedPlatform = false
                        }
                    )
                }
            }
        }

        Box {
            OutlinedButton(onClick = { expandedGenre = true }) {
                Text(
                    if (selectedGenre.isEmpty())
                        "Choose a genre"
                    else
                        selectedGenre
                )
            }

            DropdownMenu(
                expanded = expandedGenre,
                onDismissRequest =  { expandedGenre = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre) },
                        onClick = {
                            selectedGenre = genre
                            expandedGenre = false
                        }

                    )
                }
            }
        }

        OutlinedTextField(
            value = releaseYearInput,
            onValueChange = { newValue ->
                if (newValue.length <= 4 && newValue.all { it.isDigit() })
                    releaseYearInput = newValue
            },
            label = { Text("Release Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = isYearError,
            supportingText = {
                if (isYearError) Text("Enter a year between 1950 and $currentYear")
            }
        )

        Text("Status")
        GameStatus.entries.forEach { status ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status }
                )
                Text(
                    text = when (status) {
                        GameStatus.NOT_STARTED -> "Not started"
                        GameStatus.IN_PROGRESS -> "In progress"
                        GameStatus.COMPLETED -> "Completed"
                    }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Favorite")
            Switch(
                checked = isFavoriteChecked,
                onCheckedChange = { isFavoriteChecked = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowGameFormScreen() {
    PersonalGameLibraryTheme {
        GameFormScreen(
            platforms = platformList,
            genres = genreList
        )
    }
}