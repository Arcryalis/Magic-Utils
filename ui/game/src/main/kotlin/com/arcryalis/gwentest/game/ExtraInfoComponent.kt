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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

@Composable
fun ExtraInfoComponent(
    nextImageUrl: String,
    mostRecentCard: CardInfo,
    ongoingCards: List<CardInfo>,
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onListItemClicked: (CardInfo) -> Unit = {}
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = nextImageUrl,
            contentDescription = stringResource(R.string.button_show_next),
            alignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .weight(3f)
                .align(Alignment.CenterVertically)
                .clickable(onClick = onNextClicked),
        )

        AsyncImage(
            model = mostRecentCard.images.small,
            contentDescription = stringResource(R.string.button_show_history),
            alignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .weight(3f)
                .align(Alignment.CenterVertically)
                .clickable(onClick = onHistoryClicked),
        )

        OngoingCardListCard(
            cards = ongoingCards,
            modifier = Modifier
                .weight(4f)
                .align(Alignment.CenterVertically),
            onListItemClicked = onListItemClicked
        )
    }
}

@Composable
private fun OngoingCardListCard(
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
            val pagerState = rememberPagerState(
                pageCount = { cards.size }
            )

            HorizontalPager(
                state = pagerState,
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = if (isPortrait) {
                    (boxWidth/20).dp
                } else {
                    (boxWidth/12).dp
                })
            ) { index ->
                val card = cards[index]
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
    }
}
