package com.arcryalis.gwentest.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import com.arcryalis.gwentest.core.LifeCounter
import com.arcryalis.gwentest.core.theme.GwenTestTheme


@Composable
fun LifeComponent(
    players: List<PlayerInfo>,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {},
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    when (players.size) {
        1 -> LifeCounterItem(
            player = players[0],
            index = 0,
            arrowsVisible = isPortrait,
            modifier = Modifier.fillMaxSize(),
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
        )
        2 -> LifeTwoPlayerSection(
            players = players,
            arrowsVisible = isPortrait,
            modifier = modifier,
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
        )
        4 -> LifeMultiPlayerSection(
            players = players,
            arrowsVisible = isPortrait,
            modifier = modifier,
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
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
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {}
) {
    LifeCounter(
        lifeTotal = player.lifeTotal,
        backgroundColor = player.backgroundColor,
        arrowsVisible = arrowsVisible,
        modifier = modifier.fillMaxSize(),
        onUpClicked = { onPlayerUpClicked(index) },
        onDownClicked = { onPlayerDownClicked(index) },
        onLongClicked = { onPlayerLongClicked(index) },
    )
}

@Composable
private fun LifeTwoPlayerSection(
    players: List<PlayerInfo>,
    arrowsVisible: Boolean,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        LifeCounterItem(
            player = players[0],
            index = 0,
            arrowsVisible = arrowsVisible,
            modifier = Modifier.weight(1f),
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
        )

        VerticalDivider()

        LifeCounterItem(
            player = players[1],
            index = 1,
            arrowsVisible = arrowsVisible,
            modifier = Modifier
                .weight(1f)
                .rotate(180f),
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
        )
    }
}

@Composable
private fun LifeMultiPlayerSection(
    players: List<PlayerInfo>,
    arrowsVisible: Boolean,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {}
) {
    val isThreePlayer = players.size == 3

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .rotate(180f)
        ) {
            LifeCounterItem(
                player = players[0],
                index = 0,
                arrowsVisible = arrowsVisible,
                modifier = Modifier.weight(1f),
                onPlayerUpClicked = onPlayerUpClicked,
                onPlayerDownClicked = onPlayerDownClicked,
                onPlayerLongClicked = onPlayerLongClicked
            )

            VerticalDivider()

            LifeCounterItem(
                player = players[1],
                index = 1,
                arrowsVisible = arrowsVisible,
                modifier = Modifier.weight(1f),
                onPlayerUpClicked = onPlayerUpClicked,
                onPlayerDownClicked = onPlayerDownClicked,
                onPlayerLongClicked = onPlayerLongClicked
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.weight(1f)
        ) {
            if (isThreePlayer) {
                Box(
                    modifier = Modifier.weight(0.5f)
                )
            }

            LifeCounterItem(
                player = players[2],
                index = 2,
                arrowsVisible = arrowsVisible,
                modifier = Modifier.weight(1f),
                onPlayerUpClicked = onPlayerUpClicked,
                onPlayerDownClicked = onPlayerDownClicked,
                onPlayerLongClicked = onPlayerLongClicked
            )

            if (isThreePlayer) {
                Box(
                    modifier = Modifier.weight(0.5f)
                )
            } else {
                VerticalDivider()

                LifeCounterItem(
                    player = players[3],
                    index = 3,
                    arrowsVisible = arrowsVisible,
                    modifier = Modifier.weight(1f),
                    onPlayerUpClicked = onPlayerUpClicked,
                    onPlayerDownClicked = onPlayerDownClicked,
                    onPlayerLongClicked = onPlayerLongClicked
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterOnePlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(lifeTotal = "20")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterTwoPlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(lifeTotal = "20"),
                PlayerInfo(lifeTotal = "25"),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterThreePlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(
                    lifeTotal = "20",
                    backgroundColor = Color.Red.copy(
                        alpha = 0.3f
                    )
                ),
                PlayerInfo(
                    lifeTotal = "25",
                    Color.Blue.copy(
                        alpha = 0.3f
                    )
                ),
                PlayerInfo(
                    lifeTotal = "40",
                    Color.Green.copy(
                        alpha = 0.3f
                    )
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterFourPlayerPreview() {
    GwenTestTheme {
        LifeComponent(
            players = listOf(
                PlayerInfo(lifeTotal = "20",),
                PlayerInfo(lifeTotal = "25"),
                PlayerInfo(lifeTotal = "40"),
                PlayerInfo(lifeTotal = "100"),
            )
        )
    }
}
