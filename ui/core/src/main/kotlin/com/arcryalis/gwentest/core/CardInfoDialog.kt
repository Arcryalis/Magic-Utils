package com.arcryalis.gwentest.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun CardInfoDialog(
    card: CardInfo,
    showRemoveButton: Boolean,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    onAddClicked: (CardInfo) -> Unit = {},
    onRemoveClicked: (CardInfo) -> Unit = {}
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
                CardInfoDialogHeader(
                    card = card,
                    showRemoveButton = showRemoveButton,
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

                val oracleText = card.oracleText
                if (oracleText != null) {
                    Text(
                        text = oracleText,
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
private fun CardInfoDialogHeader(
    card: CardInfo,
    showRemoveButton: Boolean,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    onAddClicked: (CardInfo) -> Unit = {},
    onRemoveClicked: (CardInfo) -> Unit = {}
) {
    Row(modifier = modifier) {
        CardInfoDialogIcon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.card_info_back),
            onClicked = onCloseClicked
        )

        Spacer(modifier.weight(1f))

        if(showRemoveButton) {
            CardInfoDialogIcon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.card_info_close),
                tint = Color.Red,
                onClicked = { onRemoveClicked(card) }
            )
        } else {
            CardInfoDialogIcon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.card_info_add),
                onClicked = { onAddClicked(card) }
            )
        }
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
fun CardInfoDialogPreview() {
    GwenTestTheme {
        CardInfoDialog(
            card = CardInfo(
                name = "Card Name",
                oracleText = "Oracle Text",
                images = CardInfoImageUrls(
                    small = "smallUrl",
                    large = "largeUrl",
                    back = "backUrl"
                ),
                isOngoing = true,
            ),
            showRemoveButton = true,
            modifier = Modifier
        )
    }
}