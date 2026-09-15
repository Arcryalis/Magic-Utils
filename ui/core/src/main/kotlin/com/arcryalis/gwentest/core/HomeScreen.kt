package com.arcryalis.gwentest.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardInfo

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()){
    val state = viewModel.state.collectAsState()

    CardList(
        cardList = state.value,
        modifier = modifier
    )
}

@Composable
fun CardList(cardList: List<CardInfo>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(cardList.count()) { index ->
            val card = cardList[index]
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "Name:\n${card.name}",
                        modifier = Modifier
                            .padding(4.dp)
                    )

                    Spacer(modifier)

                    Text(
                        text = "isOngoing:\n${card.isOngoing}",
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AsyncImage(
                        model = card.imageUrl,
                        contentDescription = card.name,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                            .padding(8.dp)
                            .background(Color.Gray)
                    )
                }
            }

            if (index < cardList.count() - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun CardListPreview() {
    GwenTestTheme {
        CardList(
            listOf(
                CardInfo("Name", "https://image.url", true),
                CardInfo("Different name","http://something.else", false),
            )
        )
    }
}