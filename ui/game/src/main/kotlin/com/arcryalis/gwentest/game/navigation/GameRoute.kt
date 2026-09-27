package com.arcryalis.gwentest.game.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(
    val playerLifeTotals: List<Int>?,
    val setId: String?,
) : NavKey