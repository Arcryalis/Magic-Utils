package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardSet

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSchemeScreen: (String) -> Unit = {}
) {
    val state = viewModel.state.collectAsState()
    HomeScreen(
        state = state.value,
        modifier = modifier,
        onNavigateToSchemeScreen = onNavigateToSchemeScreen,
        onRefreshClick = viewModel::onRefresh
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToSchemeScreen: (String) -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    when (state) {
        is HomeState.Loading -> HomeLoadingScreen(
            modifier = modifier
        )

        is HomeState.Error -> HomeErrorScreen(
            modifier = modifier,
            onRefreshClick = onRefreshClick
        )

        is HomeState.Ready -> HomeReadyScreen(
            availableSets = state.availableSets,
            modifier = modifier,
            onItemClick = onNavigateToSchemeScreen
        )

    }
}

@Composable
private fun HomeLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(R.string.loading_info),
                modifier = modifier
                    .padding(top = 16.dp)
                    .width(180.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun HomeErrorScreen(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier
                .align(Alignment.Center)
                .width(180.dp)
        ) {
            Text(
                text = stringResource(R.string.error_info),
                modifier = modifier,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRefreshClick,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.refresh_button),
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HomeReadyScreen(
    availableSets: List<CardSet>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {}
) {
    LazyColumn(modifier.fillMaxSize()) {
        itemsIndexed(availableSets) { _, availableSet ->
            val name = availableSet.name
            val id = availableSet.id

            Button(
                onClick = {
                    onItemClick(id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    "$name ($id)",
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeLoadingScreenPreview() {
    GwenTestTheme {
        HomeLoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeErrorScreenPreview() {
    GwenTestTheme {
        HomeErrorScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeReadyScreenPreview() {
    GwenTestTheme {
        HomeReadyScreen(
            availableSets = listOf(
                CardSet(
                    "dci",
                    "DCI Promos"
                ),
                CardSet(
                    "oarc",
                    "Archenemy Schemes"
                ),
                CardSet(
                    "oe01",
                    "Archenemy: Nicol Bolas Schemes"
                )
            )
        )
    }
}