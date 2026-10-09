package com.arcryalis.gwentest.game.mapper

import com.arcryalis.gwentest.core.navigation.RoutePlayerInfo
import com.arcryalis.gwentest.game.PlayerInfo

fun RoutePlayerInfo.toModel(): PlayerInfo = PlayerInfo(
    name = name,
    lifeTotal = lifeTotal
)

fun List<RoutePlayerInfo>.toModel(): List<PlayerInfo> = map { it.toModel() }