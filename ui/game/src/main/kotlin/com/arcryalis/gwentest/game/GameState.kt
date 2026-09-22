package com.arcryalis.gwentest.game

import androidx.compose.ui.graphics.Color
import com.arcryalis.gwentest.data.card.model.CardInfo

data class GameState(
    val players: List<PlayerInfo>,
    val overlayState: OverlayState,
)

sealed interface OverlayState {
    data object Hidden: OverlayState

    data object LifeInput: OverlayState

    data class Individual(
        val info: CardInfo
    )

    data class Gallery(
        val cards: List<CardInfo>
    )
}

data class PlayerInfo(
    val lifeTotal: String,
    val backgroundColor: Color = Color.LightGray
)