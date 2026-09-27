package com.arcryalis.gwentest.scheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.CardInfoDialog
import com.arcryalis.gwentest.core.CardInfoGallery
import com.arcryalis.gwentest.core.FullScreenDialog
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

@Composable
fun SchemeScreen(
    modifier: Modifier = Modifier,
    viewModel: SchemeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    SchemeScreen(
        state = state.value,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        onCardClicked = viewModel::onCardClicked,
        onCloseOverlayClicked = viewModel::onCloseOverlayClicked
    )
}
@Composable
fun SchemeScreen(
    state: SchemeState,
    modifier: Modifier = Modifier,
    onCardClicked: (CardInfo) -> Unit = {},
    onCloseOverlayClicked: () -> Unit = {}
) {
    when (state) {
        SchemeState.Loading -> LoadingScreen(
            text = stringResource(R.string.schemes_loading),
            modifier = modifier
        )

        SchemeState.Error -> SchemeErrorScreen(modifier)

        is SchemeState.Ready -> {
            SchemeReadyScreen(
                cardList = state.cards,
                overlayCard = state.overlayCard,
                modifier = modifier,
                onCardClicked = onCardClicked,
                onCloseOverlayClicked = onCloseOverlayClicked,
            )
        }
    }
}

@Composable
fun SchemeErrorScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.schemes_error),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SchemeReadyScreen(
    cardList: List<CardInfo>,
    overlayCard: CardInfo?,
    modifier: Modifier = Modifier,
    onCardClicked: (CardInfo) -> Unit = {},
    onCloseOverlayClicked: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        CardInfoGallery(
            cards = cardList,
            modifier = modifier.fillMaxSize(),
            onCardClicked = onCardClicked,
        )

        if (overlayCard != null) {
            FullScreenDialog(
                modifier = Modifier,
                onDismiss = onCloseOverlayClicked
            ) {
                CardInfoDialog(
                    card = overlayCard,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchemeReadyScreenPreview() {
    GwenTestTheme {
        SchemeReadyScreen(
            cardList = emptyList(),
            overlayCard = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SchemeReadyScreenWithOverlayPreview() {
    GwenTestTheme {
        SchemeReadyScreen(
            cardList = listOf(
                CardInfo(
                    name = "c.name",
                    oracleText = "o.text",
                    images = CardInfoImageUrls(
                        small = "https://small.url",
                        large = "https://large.url"
                    ),
                    isOngoing = false,
                ),
                CardInfo(
                    name = "c2.name",
                    oracleText = "o2.text",
                    images = CardInfoImageUrls(
                        small = "https://too-small.url",
                        large = "https://too-large.url"
                    ),
                    isOngoing = true,
                )
            ),
            overlayCard = CardInfo(
                name = "Some name",
                oracleText = "Oracle text that is very long and can cover multiple lines at once. \nSecond line",
                images = CardInfoImageUrls(
                    small = "s",
                    large = "l"
                ),
                isOngoing = true,
            )
        )
    }
}
