package com.arcryalis.gwentest.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arcryalis.gwentest.core.isPortrait
import com.arcryalis.gwentest.core.theme.GwenTestTheme

private typealias PlayerSlot = @Composable (index: Int, modifier: Modifier) -> Unit

@Composable
fun LifeComponent(
    players: List<PlayerInfo>,
    modifier: Modifier = Modifier,
    onPlayerUpClick: (Int) -> Unit = {},
    onPlayerDownClick: (Int) -> Unit = {},
) {
    val arrowsVisible = isPortrait()
    val playerSlot: PlayerSlot = { index, slotModifier ->
        LifeCounterItem(
            player = players[index],
            index = index,
            arrowsVisible = arrowsVisible,
            modifier = slotModifier,
            onPlayerUpClick = onPlayerUpClick,
            onPlayerDownClick = onPlayerDownClick,
        )
    }

    when (players.size) {
        1 -> playerSlot(0, modifier)

        2 -> LifeTwoPlayerSection(modifier = modifier, playerSlot = playerSlot)

        3, 4 -> LifeMultiPlayerSection(
            isThreePlayer = players.size == 3,
            modifier = modifier,
            playerSlot = playerSlot,
        )

        else -> {
            // Nothing
        }
    }
}

@Composable
private fun LifeCounterItem(
    player: PlayerInfo,
    index: Int,
    arrowsVisible: Boolean,
    modifier: Modifier = Modifier,
    onPlayerUpClick: (Int) -> Unit = {},
    onPlayerDownClick: (Int) -> Unit = {},
) {
    LifeCounter(
        lifeTotal = player.lifeTotal.toString(),
        playerName = player.name,
        backgroundColor = player.backgroundColor,
        arrowsVisible = arrowsVisible,
        modifier = modifier.fillMaxSize(),
        onUpClick = { onPlayerUpClick(index) },
        onDownClick = { onPlayerDownClick(index) },
    )
}

@Suppress("ktlint:compose:content-slot-reused")
@Composable
private fun LifeTwoPlayerSection(
    playerSlot: PlayerSlot,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        playerSlot(0, Modifier.weight(1f))

        VerticalDivider()

        playerSlot(1, Modifier.weight(1f).rotate(180f))
    }
}

@Suppress("ktlint:compose:content-slot-reused")
@Composable
private fun LifeMultiPlayerSection(
    isThreePlayer: Boolean,
    playerSlot: PlayerSlot,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .weight(1f)
                .rotate(180f),
        ) {
            playerSlot(0, Modifier.weight(1f))

            VerticalDivider()

            playerSlot(1, Modifier.weight(1f))
        }

        HorizontalDivider()

        Row(modifier = Modifier.weight(1f)) {
            if (isThreePlayer) {
                Box(modifier = Modifier.weight(0.5f))
            }

            playerSlot(2, Modifier.weight(1f))

            if (isThreePlayer) {
                Box(modifier = Modifier.weight(0.5f))
            } else {
                VerticalDivider()

                playerSlot(3, Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LifeCounterOnePlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(name = "Player 1", lifeTotal = 20),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LifeCounterTwoPlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(name = "Player 1", lifeTotal = 20),
                PlayerInfo(name = "Player 2", lifeTotal = 25),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LifeCounterThreePlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(
                    name = "Player 1",
                    lifeTotal = 20,
                    backgroundColor = Color.Red.copy(
                        alpha = 0.3f,
                    ),
                ),
                PlayerInfo(
                    name = "",
                    lifeTotal = 25,
                    Color.Blue.copy(
                        alpha = 0.3f,
                    ),
                ),
                PlayerInfo(
                    name = "Player 3",
                    lifeTotal = 40,
                    Color.Green.copy(
                        alpha = 0.3f,
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LifeCounterFourPlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(name = "Player 1", lifeTotal = 20),
                PlayerInfo(name = "A secret", lifeTotal = 25),
                PlayerInfo(name = "", lifeTotal = 40),
                PlayerInfo(name = "", lifeTotal = 100),
            ),
        )
    }
}
