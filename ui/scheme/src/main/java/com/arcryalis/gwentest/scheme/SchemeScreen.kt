package com.arcryalis.gwentest.scheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.theme.GwenTestTheme

@Composable
fun SchemeScreen(
    modifier: Modifier = Modifier,
    viewModel: SchemeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    SchemeScreen(
        state = state.value,
        modifier = modifier,
        onSchemeClicked = viewModel::onSchemeClicked,
        onOngoingClicked = viewModel::onOngoingClicked,
        onCloseOverlayClicked = viewModel::onCloseOverlayClicked,
        onAddOngoingClicked = viewModel::onAddOngoingClicked,
        onRemoveOngoingClicked = viewModel::onRemoveOngoingClicked
    )
}
@Composable
fun SchemeScreen(
    state: SchemeState,
    modifier: Modifier = Modifier,
    onSchemeClicked: (CardUiInfo) -> Unit = {},
    onOngoingClicked: (CardUiInfo) -> Unit = {},
    onCloseOverlayClicked: () -> Unit = {},
    onAddOngoingClicked: (CardUiInfo) -> Unit = {},
    onRemoveOngoingClicked: (CardUiInfo) -> Unit = {}
) {
    when (state) {
        SchemeState.Loading -> LoadingScreen(
            text = stringResource(R.string.schemes_loading),
            modifier = modifier
        )
        is SchemeState.Ready -> {
            SchemeReadyScreen(
                cardList = state.cards,
                faceUpList = state.faceUpCards,
                ongoingCardList = state.ongoingCards,
                overlayCard = state.overlayCard,
                modifier = modifier,
                onSchemeClicked = onSchemeClicked,
                onOngoingClicked = onOngoingClicked,
                onCloseOverlayClicked = onCloseOverlayClicked,
                onAddOngoingClicked = onAddOngoingClicked,
                onRemoveOngoingClicked = onRemoveOngoingClicked
            )
        }
    }
}

@Composable
private fun SchemeReadyScreen(
    cardList: List<CardUiInfo>,
    faceUpList: List<CardUiInfo>,
    ongoingCardList: List<CardUiInfo>,
    overlayCard: CardUiInfo?,
    modifier: Modifier = Modifier,
    onSchemeClicked: (CardUiInfo) -> Unit = {},
    onOngoingClicked: (CardUiInfo) -> Unit = {},
    onCloseOverlayClicked: () -> Unit = {},
    onAddOngoingClicked: (CardUiInfo) -> Unit = {},
    onRemoveOngoingClicked: (CardUiInfo) -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        CardList(
            cardList = cardList,
            faceUpList = faceUpList,
            ongoingCardList = ongoingCardList,
            modifier = Modifier.fillMaxSize(),
            onSchemeClicked = onSchemeClicked,
            onOngoingClicked = onOngoingClicked
        )

        if (overlayCard != null) {
            OngoingCardOverlay(
                card = overlayCard,
                inOngoingList = ongoingCardList.contains(overlayCard),
                modifier = Modifier.fillMaxSize(),
                onCloseClicked = onCloseOverlayClicked,
                onAddClicked = onAddOngoingClicked,
                onRemoveClicked = onRemoveOngoingClicked
            )
        }
    }
}

@Composable
private fun CardList(
    cardList: List<CardUiInfo>,
    faceUpList: List<CardUiInfo>,
    ongoingCardList: List<CardUiInfo>,
    modifier: Modifier = Modifier,
    onSchemeClicked: (CardUiInfo) -> Unit = {},
    onOngoingClicked: (CardUiInfo) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        SchemePager(
            cardList = cardList,
            faceUpList = faceUpList,
            modifier = Modifier.weight(1f),
            onSchemeClicked = onSchemeClicked
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            thickness = 2.dp
        )

        OngoingCardList(
            ongoingCardList = ongoingCardList,
            modifier = Modifier.height(150.dp),
            onOngoingClicked = onOngoingClicked
        )
    }
}

@Composable
private fun SchemePager(
    cardList: List<CardUiInfo>,
    faceUpList: List<CardUiInfo>,
    modifier: Modifier = Modifier,
    onSchemeClicked: (CardUiInfo) -> Unit = {}
) {
    val pagerState = rememberPagerState(
        pageCount = { cardList.count() }
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
    ) { index ->
        val card = cardList[index]
        val isCardFaceUp = faceUpList.contains(card)

        AsyncImage(
            model = if (isCardFaceUp) {
                card.images.large
            } else {
                card.images.back
            },
            contentDescription = card.name,
            alignment = Alignment.Center,
            modifier = Modifier
                .clickable(
                    onClick = { onSchemeClicked(card) }
                )
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun OngoingCardList(
    ongoingCardList: List<CardUiInfo>,
    modifier: Modifier = Modifier,
    onOngoingClicked: (CardUiInfo) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(ongoingCardList) { _, card ->
            AsyncImage(
                model = card.images.small,
                contentDescription = card.name,
                alignment = Alignment.Center,
                modifier = Modifier
                    .clickable(
                        onClick = { onOngoingClicked(card) }
                    )
                    .width(120.dp)
            )
        }
    }
}

@Composable
private fun OngoingCardOverlay(
    card: CardUiInfo,
    inOngoingList: Boolean,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    onAddClicked: (CardUiInfo) -> Unit = {},
    onRemoveClicked: (CardUiInfo) -> Unit = {}
) {
    Dialog(
        onDismissRequest = { onCloseClicked() },
    ) {
        Card(
            modifier = modifier.padding(vertical = 96.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                OngoingCardOverlayHeader(
                    card = card,
                    isCardSelected = inOngoingList,
                    modifier = Modifier,
                    onCloseClicked = onCloseClicked,
                    onRemoveClicked = onRemoveClicked,
                    onAddClicked = onAddClicked
                )

                AsyncImage(
                    model = card.images.large,
                    contentDescription = card.name,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 2.dp
                )

                Text(
                    text = card.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (card.oracleText != null) {
                    Text(
                        text = card.oracleText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun OngoingCardOverlayHeader(
    card: CardUiInfo,
    isCardSelected: Boolean,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    onAddClicked: (CardUiInfo) -> Unit = {},
    onRemoveClicked: (CardUiInfo) -> Unit = {}
) {
    Row(modifier = modifier) {
        OngoingCardOverlayIcon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.overlay_back),
            onClicked = onCloseClicked
        )

        Spacer(modifier.weight(1f))

        if(isCardSelected) {
            OngoingCardOverlayIcon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.overlay_close),
                tint = Color.Red,
                onClicked = { onRemoveClicked(card) }
            )
        } else {
            OngoingCardOverlayIcon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.overlay_add),
                onClicked = { onAddClicked(card) }
            )
        }
    }
}


@Composable
private fun OngoingCardOverlayIcon(
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color = LocalContentColor.current,
    onClicked: () -> Unit = {}
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClicked)
    )
}

@Preview(showBackground = true)
@Composable
fun SchemeReadyScreenPreview() {
    GwenTestTheme {
        SchemeReadyScreen(
            cardList = emptyList(),
            ongoingCardList = emptyList(),
            faceUpList = emptyList(),
            overlayCard = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SchemeReadyScreenWithOverlayPreview() {
    GwenTestTheme {
        SchemeReadyScreen(
            cardList = emptyList(),
            ongoingCardList = emptyList(),
            faceUpList = emptyList(),
            overlayCard = CardUiInfo(
                name = "Some name",
                oracleText = "Oracle text that is very long and can cover multiple lines at once. \nSecond line",
                images = CardUiImageUrls(
                    small = "s",
                    large = "l",
                    back = "b"
                ),
                isOngoing = true,
            )
        )
    }
}
