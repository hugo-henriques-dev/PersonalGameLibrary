package com.example.personalgamelibrary.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.personalgamelibrary.data.GameStatus
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme

@Composable
fun GameFormScreen() {
    var nameInput by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }
    var releaseYearInput by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(GameStatus.NOT_STARTED) }
    var isFavoriteChecked by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Name") },
            placeholder = { Text("Type the game's name...") }
        )

        OutlinedTextField(
            value = selectedPlatform,
            onValueChange = { selectedPlatform = it },
            label = { Text("Platform") }
        )

        OutlinedTextField(
            value = selectedGenre,
            onValueChange = { selectedGenre = it },
            label = { Text("Genre") }
        )

        OutlinedTextField(
            value = releaseYearInput,
            onValueChange = { releaseYearInput = it },
            label = { Text("Release Year") }

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
        GameFormScreen()
    }
}