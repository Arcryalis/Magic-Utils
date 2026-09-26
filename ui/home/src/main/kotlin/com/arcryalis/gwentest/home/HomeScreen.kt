package com.arcryalis.gwentest.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.DropdownMenu
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardSet

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSchemeScreen: () -> Unit = {},
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> }
) {
    val state = viewModel.state.collectAsState()
    HomeScreen(
        state = state.value,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        onNavigateToGameScreen = onNavigateToGameScreen,
        onSetSelected = viewModel::onSelectSet,
        onPlayerCountSelected = viewModel::onSelectPlayerCount,
        onLifeTotalSelected = viewModel::onSelectLifeTotal,
        onRefreshClick = viewModel::onRefresh
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> },
    onSetSelected: (CardSet?) -> Unit = {},
    onPlayerCountSelected: (Int?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    when (state) {
        is HomeState.Loading -> LoadingScreen(
            text = stringResource(R.string.loading_info),
            modifier = modifier
        )

        is HomeState.Error -> HomeErrorScreen(
            modifier = modifier,
            onRefreshClick = onRefreshClick
        )

        is HomeState.Ready -> HomeReadyScreen(
            playerCountState = state.playerCount,
            startingLifeState = state.startingLife,
            availableSetsState = state.sets,
            modifier = modifier,
            onSetSelected = onSetSelected,
            onPlayerCountSelected = onPlayerCountSelected,
            onLifeTotalSelected = onLifeTotalSelected,
            onSubmitClicked = onNavigateToGameScreen,
            onRefresh = onRefreshClick
        )
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
            Modifier
                .align(Alignment.Center)
                .width(180.dp)
        ) {
            Text(
                text = stringResource(R.string.error_info),
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
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReadyScreen(
    playerCountState: HomeItemState<Int?>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onPlayerCountSelected: (Int?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> },
    onRefresh: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = false, //handled by HomeState instead
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                DropdownMenu(
                    items = playerCountState.items,
                    selectedItem = playerCountState.selectedItem,
                    titleLabel = stringResource(R.string.select_players_label),
                    onItemSelected = onPlayerCountSelected
                )

                if (playerCountState.selectedItem != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownMenu(
                        items = startingLifeState.items,
                        selectedItem = startingLifeState.selectedItem,
                        titleLabel = stringResource(R.string.select_life_label),
                        onItemSelected = onLifeTotalSelected
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenu(
                    items = availableSetsState.items,
                    selectedItem = availableSetsState.selectedItem,
                    titleLabel = stringResource(R.string.select_set_label),
                    fieldLabel = { set ->
                        set?.let { "${set.name} (${set.id.uppercase()})" }
                    },
                    onItemSelected = onSetSelected
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        onSubmitClicked(
                            playerCountState.selectedItem?.let { count ->
                                List(count) { startingLifeState.selectedItem }
                            },
                            availableSetsState.selectedItem?.id
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.select_set_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
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
            playerCountState = HomeItemState(
                items = listOf(null, 1, 2, 3, 4),
                selectedItem = 1
            ),
            startingLifeState = HomeItemState(
                items = listOf(20, 40, 60),
                selectedItem = 20
            ),
            availableSetsState = HomeItemState(
                items = listOf(
                    null,
                    CardSet("dci", "DCI Promos"),
                    CardSet("oarc", "Archenemy Schemes"),
                    CardSet("oe01", "Archenemy: Nicol Bolas Schemes")
                ),
                selectedItem = CardSet("dci", "DCI Promos")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeReadyScreenNoPlayersPreview() {
    GwenTestTheme {
        HomeReadyScreen(
            playerCountState = HomeItemState(
                items = listOf(null),
                selectedItem = null
            ),
            startingLifeState = HomeItemState(
                items = listOf(20, 40, 60),
                selectedItem = 40
            ),
            availableSetsState = HomeItemState(
                items = listOf(
                    null,
                    CardSet("dci", "DCI Promos"),
                    CardSet("oarc", "Archenemy Schemes"),
                    CardSet("oe01", "Archenemy: Nicol Bolas Schemes")
                ),
                selectedItem = CardSet("dci", "DCI Promos")
            )
        )
    }
}
