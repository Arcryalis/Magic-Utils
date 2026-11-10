package com.arcryalis.gwentest.home

data class HomeGameSettings(
    val players: List<HomePlayerInfo>,
    val startingLife: Int,
    val setId: String?
)