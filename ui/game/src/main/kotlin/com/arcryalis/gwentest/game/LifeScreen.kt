package com.arcryalis.gwentest.game

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls


@Composable
fun LifeCounterScreen(
    state: GameState = GameState(
        players = listOf(
            PlayerInfo(
                lifeTotal = "100",
                backgroundColor = Color.Red.copy(
                    alpha = 0.3f
                )
            ),
            PlayerInfo(
                lifeTotal = "100",
                Color.Blue.copy(
                    alpha = 0.3f
                )
            ),
        ),
        overlayState = OverlayState.Hidden
    ),
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {},
) {
    PortraitGameScreen(
        players = state.players,
        modifier = modifier,
        onPlayerUpClicked = onPlayerUpClicked,
        onPlayerDownClicked = onPlayerDownClicked,
        onPlayerLongClicked = onPlayerLongClicked
    )
}

@Composable
private fun PortraitGameScreen(
    players: List<PlayerInfo>,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {},
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onListItemClicked: (CardInfo) -> Unit = {}
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LifeComponent(
            players = players,
            modifier = Modifier.weight(
                if (isPortrait) 3f else 2f
            ),
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked
        )

        ExtraInfoComponent(
            nextImageUrl = "https://backs.scryfall.io/large/1/b/1b2396d4-9048-439d-96bd-354288518841.jpg?1665006146",
            mostRecentCard = CardInfo(
                name = "c2.name",
                oracleText = "o2.text",
                images = CardInfoImageUrls(
                    small = "https://cards.scryfall.io/display/front/3/c/3c05afe6-c92f-440d-b09a-cc23b15da495.webp?1783936123",
                    large = "https://too-large.url",
                    back = "https://too-back.url"
                ),
                isOngoing = true,
            ),
            ongoingCards = listOf(
                CardInfo(
                    name = "c2.name",
                    oracleText = "o2.text",
                    images = CardInfoImageUrls(
                        small = "https://cards.scryfall.io/display/front/3/c/3c05afe6-c92f-440d-b09a-cc23b15da495.webp?1783936123",
                        large = "https://too-large.url",
                        back = "https://too-back.url"
                    ),
                    isOngoing = true,
                ),
                CardInfo(
                    name = "c2.name",
                    oracleText = "o2.text",
                    images = CardInfoImageUrls(
                        small = "https://cards.scryfall.io/display/front/3/c/3c05afe6-c92f-440d-b09a-cc23b15da495.webp?1783936123",
                        large = "https://too-large.url",
                        back = "https://too-back.url"
                    ),
                    isOngoing = true,
                )
            ),
            modifier = Modifier.weight(if (isPortrait) 1f else 2f),
            onNextClicked = onNextClicked,
            onHistoryClicked = onHistoryClicked,
            onListItemClicked = onListItemClicked
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LifeCounterScreenPreview() {
    GwenTestTheme {
        LifeCounterScreen()
    }
}