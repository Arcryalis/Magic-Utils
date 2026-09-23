package com.arcryalis.gwentest.core

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
            .padding(8.dp)
            .fillMaxSize()
    ) {
        CardInfoGalleryHeader(
            modifier = Modifier,
            onCloseClicked = onCloseClicked
        )

        LazyColumn(

        ) { card ->

            AsyncImage(
                model = card.images.small,
                contentDescription = card.name,
                alignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )

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
                )
            ),
            modifier = Modifier
        )
    }
}