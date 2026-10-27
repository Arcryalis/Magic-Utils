package com.arcryalis.gwentest.navigation

import com.arcryalis.gwentest.game.navigation.GameRoute
import com.arcryalis.gwentest.game.navigation.RoutePlayerInfo
import com.arcryalis.gwentest.home.HomeGameSettings

fun HomeGameSettings.toGameRoute(): GameRoute = GameRoute(
    playerSettings = players.map {
        RoutePlayerInfo(
            name = it.name,
            lifeTotal = startingLife,
            color = it.color
        )
    }.ifEmpty { null },
    setId = setId
)
