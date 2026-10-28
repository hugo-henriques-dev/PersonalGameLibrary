package com.example.personalgamelibrary.ui.navigation

/**
 * Rotas de navegação da aplicação, definidas num único sítio para evitar
 * textos soltos espalhados pelo código.
 */
object Routes {
    const val LIST = "list"

    const val GAME_ID_ARG = "gameId"

    /** Valor do argumento quando o formulário é aberto para adicionar um jogo novo. */
    const val NO_GAME_ID = -1

    /** Padrão da rota do formulário; `gameId` é opcional (ausente = adicionar). */
    const val FORM = "form?$GAME_ID_ARG={$GAME_ID_ARG}"

    /**
     * Constrói a rota concreta do formulário.
     *
     * :param gameId: id do jogo a editar, ou null para adicionar um jogo novo
     * :returns: a rota pronta a passar a `navController.navigate`
     */
    fun form(gameId: Int? = null): String =
        if (gameId == null) "form" else "form?$GAME_ID_ARG=$gameId"
}