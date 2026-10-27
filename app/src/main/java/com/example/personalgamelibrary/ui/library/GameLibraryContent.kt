package com.example.personalgamelibrary.ui.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.personalgamelibrary.GameList
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.genreList
import com.example.personalgamelibrary.data.platformList
import com.example.personalgamelibrary.data.sampleGames

@Composable
fun GameLibraryContent(modifier: Modifier = Modifier) {
    val games = remember {
        mutableStateListOf<VideoGame>().apply { addAll(sampleGames) }
    }
    var isFormShown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (!isFormShown) {
                FloatingActionButton(onClick = { isFormShown = true }) {
                    Text("+")
                }
            }
        }
    ) { innerPadding ->
        if (isFormShown) {
            GameFormScreen(
                platforms = platformList,
                genres = genreList,
                onSave = { game ->
                    games.add(game)
                    isFormShown = false
                },
                onCancel = { isFormShown = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            GameList(
                games = games,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}