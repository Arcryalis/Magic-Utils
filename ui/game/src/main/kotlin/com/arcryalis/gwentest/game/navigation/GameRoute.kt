package com.arcryalis.gwentest.game.navigation

import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(
    val playerSettings: List<RoutePlayerInfo>?,
    val setId: String?,
) : NavKey

@Serializable
data class RoutePlayerInfo(
    val name: String,
    val lifeTotal: Int,
    val colorValue: Long = 0L
) {
    val color: Color
        get() = Color(colorValue.toULong())

    companion object {
        operator fun invoke(name: String, lifeTotal: Int, color: Color): RoutePlayerInfo {
            return RoutePlayerInfo(name, lifeTotal, color.value.toLong())
        }
    }
}