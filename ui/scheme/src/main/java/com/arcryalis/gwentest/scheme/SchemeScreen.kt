package com.arcryalis.gwentest.scheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
fun SchemeScreen(
    modifier: Modifier = Modifier,
    viewModel: SchemeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    SchemeScreen(
        state = state.value,
        modifier = modifier,
        addCardToOngoingList = viewModel::addCardToOngoingList
    )
}
@Composable
fun SchemeScreen(
    state: SchemeState,
    modifier: Modifier = Modifier,
    addCardToOngoingList: (CardInfo) -> Unit = {}
) {
    when (state) {
        SchemeState.Loading -> {
            Text("Loading") //TODO
        }
        is SchemeState.Ready -> {
            CardList(
                cardList = state.cards,
                ongoingCardList = state.ongoingCards,
                modifier = modifier,
                onOngoingCardAppear = addCardToOngoingList
            )
        }
    }
}

@Composable
fun CardList(
    cardList: List<CardInfo>,
    ongoingCardList: List<CardInfo>,
    modifier: Modifier = Modifier,
    onOngoingCardAppear: (CardInfo) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        val pagerState = rememberPagerState(
            pageCount = { cardList.count() }
        )

        HorizontalPager(
            state = pagerState,
            modifier = modifier.weight(1f)
        ) { index ->
            val card = cardList[index]

            if (index == pagerState.currentPage && card.isOngoing) {
                onOngoingCardAppear(card)
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = card.images.large,
                    contentDescription = card.name,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxSize()
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            thickness = 2.dp
        )

        LazyRow(
            modifier = Modifier
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(ongoingCardList) { _, card ->
                AsyncImage(
                    model = card.images.back,
                    contentDescription = card.name,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .height(100.dp)
                        .padding(horizontal = 8.dp)
                        .background(Color.Blue)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CardListPreview() {
//    GwenTestTheme {
//        CardList(
//            listOf(
//                CardInfo("Name", "https://image.url", true),
//                CardInfo("Different name","http://something.else", false),
//            )
//        )
//    }
//}