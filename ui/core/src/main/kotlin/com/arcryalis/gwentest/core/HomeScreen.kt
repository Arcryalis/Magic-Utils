package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.CardInfo

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()){
    val state = viewModel.state.collectAsState()

    CardList(
        cardList = state.value,
        modifier = modifier
    )

//    Greeting(
//        name = state.value,
//        modifier = modifier
//    )
}

@Composable
fun CardList(cardList: List<CardInfo>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(cardList.count()) { index ->
            val card = cardList[index]
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Name:\n${card.name}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
                Text(
                    text = "isOngoing:\n${card.isOngoing}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }

            Row(
                modifier = Modifier
            ) {
                Text(
                    text = "Name:\n${card.imageUrl}",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
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
                CardInfo("Loading", "Some url", true),
                CardInfo("Loading","Some url",  false),
            )
        )
    }
}