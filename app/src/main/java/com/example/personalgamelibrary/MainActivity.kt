package com.example.personalgamelibrary

import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            val darkOverride by gameViewModel.darkTheme.collectAsState()
            val darkTheme = darkOverride ?: isSystemInDarkTheme()

            // Ajusta a cor dos ícones das barras do sistema ao tema da app
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) {
                        darkTheme
                    },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) {
                        darkTheme
                    }
                )
                onDispose {}
            }

            PersonalGameLibraryTheme(darkTheme = darkTheme) {
                GameLibraryContent(gameViewModel = gameViewModel)
            }
        }
    }
}
