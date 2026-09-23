package com.arcryalis.gwentest.game.navigation

import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(
    val playerLifeTotals: List<Int>?,
    val setId: String?,
)