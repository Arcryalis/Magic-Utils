package com.arcryalis.gwentest.core.navigation

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
)