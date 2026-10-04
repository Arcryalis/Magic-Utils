package com.arcryalis.gwentest.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
        onNavigateToSchemeScreen = onNavigateToSchemeScreen,
        onSetSelected = viewModel::onSelectSet,
        onLifeTotalSelected = viewModel::onSelectLifeTotal,
        onRefreshClick = viewModel::onRefresh,
        onDownloadSchemesClick = viewModel::onDownloadSchemes,
        onAddPlayer = viewModel::addPlayer,
        onRemovePlayer = viewModel::removePlayer
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> },
    onNavigateToSchemeScreen: () -> Unit = {},
    onSetSelected: (CardSet?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onRemovePlayer: (Int) -> Unit = {}
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
            modifier = modifier,
            onSetSelected = onSetSelected,
            onLifeTotalSelected = onLifeTotalSelected,
            onSubmitClicked = onNavigateToGameScreen,
            onNavigateToSchemeScreen = onNavigateToSchemeScreen,
            onRefresh = onRefreshClick,
            onDownloadSchemesClick = onDownloadSchemesClick,
            onAddPlayer = onAddPlayer,
            onRemovePlayer = onRemovePlayer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReadyScreen(
    players: List<String>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> },
    onNavigateToSchemeScreen: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onRemovePlayer: (Int) -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            HomeScreenReadyContent(
                players = players,
                startingLifeState = startingLifeState,
                availableSetsState = availableSetsState,
                modifier = Modifier,
                onSetSelected = onSetSelected,
                onLifeTotalSelected = onLifeTotalSelected,
                onNavigateToSchemeScreen = onNavigateToSchemeScreen,
                onDownloadSchemesClick = onDownloadSchemesClick,
                onAddPlayer = onAddPlayer,
                onRemovePlayer = onRemovePlayer
            )

            Spacer(Modifier.weight(1f))

            HomeReadyFooterContent(
                players = players,
                startingLifeState = startingLifeState,
                availableSetsState = availableSetsState,
                onSubmitClicked = onSubmitClicked
            )
        }
    }
}


@Composable
private fun HomeReadyFooterContent(
    players: List<String>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    modifier: Modifier = Modifier,
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> }
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
    players: List<String>,
    selectedStartingLife: Int,
    selectedSet: CardSet?,
    modifier: Modifier = Modifier,
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> }
) {
    Button(
        onClick = {
            onSubmitClicked(
                if (players.isNotEmpty()) {
                    List(players.size) { selectedStartingLife }
                } else {
                    null
                },
                selectedSet?.id
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
            players = listOf("Player 1", "Player 2"),
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
