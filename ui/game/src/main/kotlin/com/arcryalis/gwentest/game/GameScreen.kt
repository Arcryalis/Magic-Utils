package com.arcryalis.gwentest.game

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.CardInfoDialog
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls


@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    GameScreen(
        state = state.value,
        modifier = modifier,
        onPlayerUpClicked = viewModel::increasePlayerLife,
        onPlayerDownClicked = viewModel::decreasePlayerLife,
        onPlayerLongClicked = {}, //TODO
        onNextClicked = viewModel::revealNextCard,
        onHistoryClicked = viewModel::showRevealedCardsGallery,
        onListItemClicked = viewModel::showCardOnOverlay,
        onOverlayCloseClicked = viewModel::hideOverlay,
        onOverlayAddClicked = viewModel::addCardToExtras,
        onOverlayRemoveClicked = viewModel::removeCardFromExtras
    )
}

@Composable
fun GameScreen(
    state: GameState,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {},
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onListItemClicked: (CardInfo) -> Unit = {},
    onOverlayCloseClicked: () -> Unit = {},
    onOverlayAddClicked: (CardInfo) -> Unit = {},
    onOverlayRemoveClicked: (CardInfo) -> Unit = {}
) {

    when (state) {
        is GameState.Loading -> LoadingScreen(
            text = stringResource(R.string.loading),
            modifier = modifier
        )

        is GameState.Ready -> GameReadyScreen(
            state = state,
            modifier = modifier,
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked,
            onNextClicked = onNextClicked,
            onHistoryClicked = onHistoryClicked,
            onListItemClicked = onListItemClicked,
            onOverlayCloseClicked = onOverlayCloseClicked,
            onOverlayAddClicked = onOverlayAddClicked,
            onOverlayRemoveClicked = onOverlayRemoveClicked
        )
    }
}


@Composable
private fun GameReadyScreen(
    state: GameState.Ready,
    modifier: Modifier = Modifier,
    onPlayerUpClicked: (Int) -> Unit = {},
    onPlayerDownClicked: (Int) -> Unit = {},
    onPlayerLongClicked: (Int) -> Unit = {},
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onListItemClicked: (CardInfo) -> Unit = {},
    onOverlayCloseClicked: () -> Unit = {},
    onOverlayAddClicked: (CardInfo) -> Unit = {},
    onOverlayRemoveClicked: (CardInfo) -> Unit = {}
) {
    Box(modifier = modifier) {
        GameScreenContent(
            players = state.players,
            deckSettings = state.deckSettings,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            onPlayerUpClicked = onPlayerUpClicked,
            onPlayerDownClicked = onPlayerDownClicked,
            onPlayerLongClicked = onPlayerLongClicked,
            onNextClicked = onNextClicked,
            onHistoryClicked = onHistoryClicked,
            onListItemClicked = onListItemClicked
        )

        if (state.overlayState is OverlayState.Visible) {
            GameOverlayContent(
                state = state.overlayState,
                modifier = Modifier.fillMaxSize(),
                onCloseClicked = onOverlayCloseClicked,
                onAddClicked = onOverlayAddClicked,
                onRemoveClicked = onOverlayRemoveClicked
            )
        }
    }
}

@Composable
private fun GameScreenContent(
    players: List<PlayerInfo>?,
    deckSettings: DeckSettings?,
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
        val primaryWeight = if (isPortrait) 3f else 2f
        if (!players.isNullOrEmpty()) {
            LifeComponent(
                players = players,
                modifier = Modifier.weight(primaryWeight),
                onPlayerUpClicked = onPlayerUpClicked,
                onPlayerDownClicked = onPlayerDownClicked,
                onPlayerLongClicked = onPlayerLongClicked
            )
        }

        if (deckSettings != null) {
            DeckComponent(
                nextImageUrl = deckSettings.nextCardUrl,
                mostRecentCard = deckSettings.revealedCards?.firstOrNull(),
                extraCardList = deckSettings.extraCardList,
                modifier = Modifier.weight(if (isPortrait) 1f else 2f),
                onNextClicked = onNextClicked,
                onHistoryClicked = onHistoryClicked,
                onListItemClicked = onListItemClicked
            )
        }
    }
}

@Composable
fun GameOverlayContent(
    state: OverlayState.Visible,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    onAddClicked: (CardInfo) -> Unit = {},
    onRemoveClicked: (CardInfo) -> Unit = {}
) {
    when (state) {
        is OverlayState.Visible.Individual -> CardInfoDialog(
            card = state.info,
            showRemoveButton = true,
            modifier = modifier,
            onCloseClicked = onCloseClicked,
            onAddClicked = onAddClicked,
            onRemoveClicked = onRemoveClicked
        )
        else -> {} //TODO
    }
}


@Preview(showBackground = true)
@Composable
fun GameScreenLifePreview() {
    GwenTestTheme {
        GameScreen(
            state = GameState.Ready(
                players = listOf(
                    PlayerInfo(lifeTotal = "20",),
                    PlayerInfo(lifeTotal = "25"),
                    PlayerInfo(lifeTotal = "40"),
                    PlayerInfo(lifeTotal = "100")
                ),
                deckSettings = null,
                overlayState = OverlayState.Hidden
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenDeckPreview() {
    GwenTestTheme {
        GameScreen(
            state = GameState.Ready(
                players = null,
                deckSettings = DeckSettings(
                    nextCardUrl = "some url",
                    revealedCards = null,
                    extraCardList = emptyList()
                ),
                overlayState = OverlayState.Hidden
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenFullPreview() {
    GwenTestTheme {
        GameScreen(
            state = GameState.Ready(
                players = listOf(
                    PlayerInfo(lifeTotal = "100"),
                    PlayerInfo(lifeTotal = "100"),
                ),
                deckSettings = DeckSettings(
                    nextCardUrl = "https://backs.scryfall.io/large/1/b/1b2396d4-9048-439d-96bd-354288518841.jpg?1665006146",
                    revealedCards = listOf(
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
                    extraCardList = listOf(
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
                ),
                overlayState = OverlayState.Hidden
            )
        )
    }
}