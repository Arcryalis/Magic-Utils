package com.arcryalis.gwentest.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

@Composable
fun CardInfoGallery(
    cards: List<CardInfo>,
    modifier: Modifier = Modifier,
    onCardClicked: (CardInfo) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(96.dp),
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(cards) { card ->
            AsyncImage(
                model = card.images.small,
                contentDescription = card.name,
                alignment = Alignment.Center,
                modifier = Modifier
                    .height(128.dp)
                    .clickable(onClick = { onCardClicked(card) })
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardInfoGalleryPreview() {
    GwenTestTheme {
        CardInfoGallery(
            cards = listOf(
                CardInfo(
                    name = "Card Name",
                    oracleText = "Oracle Text",
                    images = CardInfoImageUrls(
                        small = "smallUrl",
                        large = "largeUrl"
                    ),
                    isOngoing = true,
                ),
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
                    name = "3rd name",
                    oracleText = "3rd text",
                    images = CardInfoImageUrls(
                        small = "small3Url",
                        large = "large3Url"
                    ),
                    isOngoing = false,
                )
            ),
            modifier = Modifier
        )
    }
}