package com.example.personalgamelibrary.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Retira o foco (e fecha o teclado) quando o utilizador toca fora de um campo
 * de texto ou começa a fazer scroll no conteúdo.
 *
 * Deve ser aplicado a um contentor que envolva os campos de texto e a área
 * com scroll. Se o contentor tiver `verticalScroll`, este modifier tem de vir
 * antes dele na cadeia.
 */
@Composable
fun Modifier.clearFocusOnTapOrScroll(): Modifier {
    val focusManager = LocalFocusManager.current

    val scrollConnection = remember(focusManager) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                focusManager.clearFocus()
                return Offset.Zero // não consome o scroll, o conteúdo move-se normalmente
            }
        }
    }

    return this
        .nestedScroll(scrollConnection)
        .pointerInput(focusManager) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        }
}