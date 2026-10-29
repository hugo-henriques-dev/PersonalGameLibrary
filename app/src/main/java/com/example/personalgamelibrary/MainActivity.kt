package com.example.personalgamelibrary

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.personalgamelibrary.ui.library.GameLibraryContent
import com.example.personalgamelibrary.ui.library.GameViewModel
import com.example.personalgamelibrary.ui.theme.PersonalGameLibraryTheme

/**
 * Activity principal da aplicação.
 *
 * Configura o layout Edge-to-Edge com barras de sistema transparentes
 * cujos ícones se ajustam dinamicamente ao tema (claro ou escuro),
 * e inicializa o [GameViewModel] com a [GameViewModel.Factory].
 */
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
                val transparent = Color.TRANSPARENT
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        transparent, transparent) {
                        darkTheme
                    },
                    navigationBarStyle = SystemBarStyle.auto(
                        transparent, transparent) {
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
