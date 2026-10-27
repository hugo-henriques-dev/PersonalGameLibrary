package com.example.personalgamelibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.personalgamelibrary.data.VideoGame
import com.example.personalgamelibrary.data.sampleGames
import com.example.personalgamelibrary.ui.library.GameLibraryContent
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PersonalGameLibraryTheme {
                GameLibraryContent()
            }
        }
    }
}

@Composable
fun GameItem(game: VideoGame, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
            .padding(30.dp)
    ) {
        Text(text = game.name)
        Text(text = game.platform)
        Text(text = game.genre)
        Text(text = game.releaseYear.toString())
        Text(text = game.status.name)
        Text(text = game.dateAdded.toString())
        Text(text = game.isFavorite.toString())
        Text(text = game.coverImage ?: "No cover")
    }
}

@Composable
fun GameList(games: List<VideoGame>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(games) { game ->
            GameItem(game = game)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowGameListPreview() {
    PersonalGameLibraryTheme {
        GameList(games = sampleGames)
    }
}