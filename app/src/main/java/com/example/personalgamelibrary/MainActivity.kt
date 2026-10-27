package com.example.personalgamelibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.personalgamelibrary.ui.library.GameLibraryContent
import com.example.personalgamelibrary.ui.library.GameViewModel
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val gameLibraryApplication = application as GameLibraryApplication
        val gameViewModel = ViewModelProvider(
            this,
            GameViewModel.Factory(gameLibraryApplication)
        )[GameViewModel::class.java]

        setContent {
            PersonalGameLibraryTheme {
                GameLibraryContent(gameViewModel = gameViewModel)
            }
        }
    }
}
