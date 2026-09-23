package com.arcryalis.gwentest.game

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcryalis.gwentest.data.card.model.CardInfo


@Composable
fun FulLScreenDeckComponent(
) {

}


@Composable
fun DeckComponent(
    nextImageUrl: String?,
    mostRecentCard: CardInfo?,
    extraCardList: List<CardInfo>,
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onListItemClicked: (CardInfo) -> Unit = {}
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (nextImageUrl != null) {
            AsyncImage(
                model = nextImageUrl,
                contentDescription = stringResource(R.string.button_show_next),
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = onNextClicked),
            )
        } else {
            Spacer(modifier = Modifier.weight(3f))
        }

        if (mostRecentCard != null) {
            AsyncImage(
                model = mostRecentCard.images.small,
                contentDescription = stringResource(R.string.button_show_history),
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = onHistoryClicked),
            )
        } else {
            Spacer(modifier = Modifier.weight(3f))
        }

        ExtraCardListCard(
            cards = extraCardList,
            modifier = Modifier
                .weight(2f)
                .align(Alignment.CenterVertically),
            onListItemClicked = onListItemClicked
        )
    }
}

@Composable
private fun ExtraCardListCard(
    cards: List<CardInfo>,
    modifier: Modifier = Modifier,
    onListItemClicked: (CardInfo) -> Unit = {}
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    var boxWidth by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier.drawBehind(
            onDraw = { boxWidth = size.width }
        )
    ) {
        Card(
            modifier = modifier
                .align(Alignment.Center)
                .padding(8.dp)
        ) {

            LazyRow(
                modifier = modifier,

            ) {
                itemsIndexed(cards) { _, card ->
                    AsyncImage(
                        model = card.images.small,
                        contentDescription = stringResource(R.string.button_show_history),
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                            .clickable(onClick = { onListItemClicked(card) }),
                    )
                }

            }
//
//            val pagerState = rememberPagerState(
//                pageCount = { cards.size }
//            )
//
//            HorizontalPager(
//                state = pagerState,
//                modifier = modifier,
//                contentPadding = PaddingValues(horizontal = if (isPortrait) {
//                    (boxWidth/20).dp
//                } else {
//                    (boxWidth/12).dp
//                })
//            ) { index ->
//                val card = cards[index]
//                AsyncImage(
//                    model = card.images.small,
//                    contentDescription = stringResource(R.string.button_show_history),
//                    alignment = Alignment.Center,
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .fillMaxSize()
//                        .clickable(onClick = { onListItemClicked(card) }),
//                )
//            }
        }
    }
}
