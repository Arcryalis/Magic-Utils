package com.arcryalis.gwentest.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
    onCloseClicked: () -> Unit = {},
    onCardClicked: (CardInfo) -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        CardInfoGalleryHeader(
            modifier = Modifier,
            onCloseClicked = onCloseClicked
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(96.dp),
            modifier = Modifier
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
}

@Composable
private fun CardInfoGalleryHeader(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
) {
    Row(modifier = modifier) {
        CardInfoDialogIcon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.card_info_back),
            onClicked = onCloseClicked
        )
    }
}


@Composable
private fun CardInfoDialogIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClicked: () -> Unit = {}
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClicked)
    )
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
                        large = "largeUrl",
                        back = "backUrl"
                    ),
                    isOngoing = true,
                ),
                CardInfo(
                    name = "c.name",
                    oracleText = "o.text",
                    images = CardInfoImageUrls(
                        small = "https://small.url",
                        large = "https://large.url",
                        back = "https://back.url"
                    ),
                    isOngoing = false,
                ),
                CardInfo(
                    name = "3rd name",
                    oracleText = "3rd text",
                    images = CardInfoImageUrls(
                        small = "small3Url",
                        large = "large3Url",
                        back = "back3Url"
                    ),
                    isOngoing = false,
                )
            ),
            modifier = Modifier
        )
    }
}