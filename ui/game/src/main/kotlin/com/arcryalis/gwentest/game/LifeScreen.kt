package com.arcryalis.gwentest.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.game.R


@Composable
fun LifeCounterScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LifeTwoPlayerLifeSection(
            modifier = Modifier.weight(1f)
        )

        LifeScreenBottomDrawer(
            modifier = Modifier
                .height(200.dp)
                .fillMaxSize()
        )
    }
}

@Composable
private fun LifeTwoPlayerLifeSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        LifeCounter(
            lifeTotal = "100",
            backgroundColor = Color.Red.copy(
                alpha = 0.3f
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        )

        LifeCounter(
            lifeTotal = "100",
            backgroundColor = Color.Blue.copy(
                alpha = 0.3f
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .rotate(180f)
        )
    }
}

@Composable
private fun LifeScreenBottomDrawer(
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit = {},
    onHistoryClicked: () -> Unit = {},
    onExtraClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

//        Icon(
//            imageVector = Icons.Default.AddCircle,
//            contentDescription = stringResource(R.string.button_show_next),
//            modifier = modifier
//                .weight(1f)
//                .align(Alignment.CenterVertically)
//                .height(12.dp)
//        )
//
        AsyncImage(
            model = "https://backs.scryfall.io/large/1/b/1b2396d4-9048-439d-96bd-354288518841.jpg?1665006146",
            contentDescription = stringResource(R.string.button_show_next),
            alignment = Alignment.Center,
            modifier = Modifier
                .clickable(onClick = onNextClicked)
                .padding(horizontal = 8.dp)
                .weight(1f)
                .align(Alignment.CenterVertically),
        )

        AsyncImage(
            model = "https://cards.scryfall.io/display/front/3/c/3c05afe6-c92f-440d-b09a-cc23b15da495.webp?1783936123",
            contentDescription = stringResource(R.string.button_show_history),
            alignment = Alignment.Center,
            modifier = Modifier
                .clickable(onClick = onHistoryClicked)
                .padding(horizontal = 8.dp)
                .weight(1f)
                .align(Alignment.CenterVertically),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Card(
                modifier = Modifier.align(Alignment.Center)
            ) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = "TODO",
//                    modifier = Modifier
//                        .clickable(onClick = onExtraClicked)
//                        .size(48.dp)
//                        .background(Color.Magenta)
//                )

                androidx.compose.foundation.layout.Grid(

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterScreenPreview() {
    GwenTestTheme {
        LifeCounterScreen()
    }
}