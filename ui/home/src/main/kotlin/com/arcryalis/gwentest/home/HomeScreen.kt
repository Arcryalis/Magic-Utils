package com.arcryalis.gwentest.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.R as CoreR
import com.arcryalis.gwentest.core.navigation.GameRoute
import com.arcryalis.gwentest.core.navigation.RoutePlayerInfo
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardSet

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSchemeScreen: () -> Unit = {},
    onNavigateToGameScreen: (GameRoute) -> Unit = { _ -> },
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state.value,
        modifier = modifier.fillMaxSize(),
        onNavigateToGameScreen = onNavigateToGameScreen,
        onNavigateToSchemeScreen = onNavigateToSchemeScreen,
        onSetSelected = viewModel::onSelectSet,
        onLifeTotalSelected = viewModel::onSelectLifeTotal,
        onRefreshClick = viewModel::onRefresh,
        onDownloadSchemesClick = viewModel::onDownloadSchemes,
        onAddPlayer = viewModel::addPlayer,
        onRemovePlayer = viewModel::removePlayer,
        onPlayerNameChange = viewModel::updatePlayerName,
        onPlayerColorChange = viewModel::updatePlayerColor
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToGameScreen: (GameRoute) -> Unit = { _ -> },
    onNavigateToSchemeScreen: () -> Unit = {},
    onSetSelected: (CardSet?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onRemovePlayer: (Int) -> Unit = {},
    onPlayerNameChange: (Int, String) -> Unit = { _, _ -> },
    onPlayerColorChange: (Int, Color) -> Unit = { _, _ -> }
) {
    when (state) {
        is HomeState.Initial -> LoadingScreen(
            text = null,
            modifier = modifier
        )

        is HomeState.Ready -> HomeReadyScreen(
            players = state.players,
            startingLifeState = state.startingLife,
            availableSetsState = state.sets,
            isRefreshing = state.isRefreshing,
            availablePlayerColors = state.playerColorOptions,
            modifier = modifier,
            onSubmit = onNavigateToGameScreen,
            onSetSelected = onSetSelected,
            onLifeTotalSelected = onLifeTotalSelected,
            onNavigateToSchemeScreen = onNavigateToSchemeScreen,
            onRefresh = onRefreshClick,
            onDownloadSchemesClick = onDownloadSchemesClick,
            onAddPlayer = onAddPlayer,
            onRemovePlayer = onRemovePlayer,
            onPlayerNameChange = onPlayerNameChange,
            onPlayerColorChange = onPlayerColorChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReadyScreen(
    players: List<HomePlayerInfo>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    isRefreshing: Boolean,
    availablePlayerColors: List<Int?>,
    modifier: Modifier = Modifier,
    onSubmit: (GameRoute) -> Unit = { _ -> },
    onSetSelected: (CardSet?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onNavigateToSchemeScreen: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onRemovePlayer: (Int) -> Unit = {},
    onPlayerNameChange: (Int, String) -> Unit = { _, _ -> },
    onPlayerColorChange: (Int, Color) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // No title
                },
                actions = {
                    IconButton(
                        onClick = {
                            //TODO
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = stringResource(R.string.open_camera)
                        )
                    }
                }
            )
        },
        bottomBar = {
            HomeReadyFooterContent(
                players = players,
                startingLifeState = startingLifeState,
                availableSetsState = availableSetsState,
                modifier = Modifier.padding(20.dp),
                onSubmitClicked = onSubmit
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            HomeScreenReadyContent(
                players = players,
                startingLifeState = startingLifeState,
                availableSetsState = availableSetsState,
                availablePlayerColors = availablePlayerColors,
                modifier = Modifier.fillMaxSize(),
                onSetSelected = onSetSelected,
                onLifeTotalSelected = onLifeTotalSelected,
                onNavigateToSchemeScreen = onNavigateToSchemeScreen,
                onDownloadSchemesClick = onDownloadSchemesClick,
                onAddPlayer = onAddPlayer,
                onRemovePlayer = onRemovePlayer,
                onPlayerNameChange = onPlayerNameChange,
                onPlayerColorChange = onPlayerColorChange
            )
        }
    }
}

@Composable
fun HomeReadyFooterContent(
    players: List<HomePlayerInfo>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    modifier: Modifier = Modifier,
    onSubmitClicked: (GameRoute) -> Unit = { _ -> }
) {
    Column(modifier = modifier) {
        HorizontalDivider(modifier = Modifier)

        HomeSubmitButton(
            players = players,
            selectedStartingLife = startingLifeState.selectedItem,
            selectedSet = availableSetsState.selectedItem,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            onSubmitClicked = onSubmitClicked
        )
    }
}

@Composable
private fun HomeSubmitButton(
    players: List<HomePlayerInfo>,
    selectedStartingLife: Int,
    selectedSet: CardSet?,
    modifier: Modifier = Modifier,
    onSubmitClicked: (GameRoute) -> Unit = { _ -> }
) {
    Button(
        onClick = {
            onSubmitClicked(
                GameRoute(
                    playerSettings = if (players.isNotEmpty()) {
                        players.map {
                            RoutePlayerInfo(
                                name = it.name,
                                lifeTotal = selectedStartingLife,
                                color = it.color
                            )
                        }
                    } else {
                        null
                    },
                    setId = selectedSet?.id
                )
            )
        },
        modifier = modifier,
        enabled = players.isNotEmpty() || selectedSet != null
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

@Preview(showBackground = true)
@Composable
fun HomeReadyScreenPreview() {
    GwenTestTheme {
        HomeReadyScreen(
            players = listOf(
                HomePlayerInfo("Player 1", Color.Red),
                HomePlayerInfo("", Color.Unspecified),
                HomePlayerInfo("Another player with a long name", Color.Blue)
            ),
            availablePlayerColors = listOf(null, CoreR.color.player_color_red, CoreR.color.player_color_blue, CoreR.color.player_color_green),
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
            ),
            isRefreshing = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeReadyScreenNoPlayersPreview() {
    GwenTestTheme {
        HomeReadyScreen(
            players = emptyList(),
            availablePlayerColors = listOf(null, CoreR.color.player_color_red, CoreR.color.player_color_blue, CoreR.color.player_color_green),
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
            ),
            isRefreshing = false
        )
    }
}
