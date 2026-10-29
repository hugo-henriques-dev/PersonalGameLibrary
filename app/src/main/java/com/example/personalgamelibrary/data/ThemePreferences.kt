package com.example.personalgamelibrary.data

import android.content.Context
import androidx.core.content.edit

/**
 * Guarda no dispositivo a escolha de tema do utilizador (claro ou escuro).
 *
 * @param context contexto usado para abrir o ficheiro de preferências
 */
class ThemePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    /**
     * Lê a escolha guardada.
     *
     * @return true para escuro, false para claro, ou null se o utilizador ainda não escolheu
     */
    fun load(): Boolean? =
        if (prefs.contains(KEY_DARK_THEME)) prefs.getBoolean(KEY_DARK_THEME, false) else null

    /**
     * Guarda a escolha do utilizador.
     *
     * @param darkTheme true para escuro, false para claro
     */
    fun save(darkTheme: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, darkTheme) }
    }

    private companion object {
        const val FILE_NAME = "settings"
        const val KEY_DARK_THEME = "dark_theme"
    }
}