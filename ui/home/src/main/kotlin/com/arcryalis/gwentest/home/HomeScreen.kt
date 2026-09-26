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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
        onPlayerCountSelected = viewModel::onSelectPlayerCount,
        onLifeTotalSelected = viewModel::onSelectLifeTotal,
        onRefreshClick = viewModel::onRefresh,
        onDownloadSchemesClick = viewModel::onDownloadSchemes
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> },
    onNavigateToSchemeScreen: () -> Unit = {},
    onSetSelected: (CardSet?) -> Unit = {},
    onPlayerCountSelected: (Int?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {}
) {
    when (state) {
        is HomeState.Initial -> LoadingScreen(
            text = null,
            modifier = modifier
        )

        is HomeState.Ready -> HomeReadyScreen(
            playerCountState = state.playerCount,
            startingLifeState = state.startingLife,
            availableSetsState = state.sets,
            isRefreshing = state.isRefreshing,
            modifier = modifier,
            onSetSelected = onSetSelected,
            onPlayerCountSelected = onPlayerCountSelected,
            onLifeTotalSelected = onLifeTotalSelected,
            onSubmitClicked = onNavigateToGameScreen,
            onNavigateToSchemeScreen = onNavigateToSchemeScreen,
            onRefresh = onRefreshClick,
            onDownloadSchemesClick = onDownloadSchemesClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReadyScreen(
    playerCountState: HomeItemState<Int?>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onPlayerCountSelected: (Int?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> },
    onNavigateToSchemeScreen: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    DropdownMenu(
                        items = playerCountState.items,
                        selectedItem = playerCountState.selectedItem,
                        titleLabel = stringResource(R.string.select_players_label),
                        modifier = Modifier.fillMaxWidth(),
                        onItemSelected = onPlayerCountSelected
                    )
                }

                if (playerCountState.selectedItem != null) {
                    item {
                        DropdownMenu(
                            items = startingLifeState.items,
                            selectedItem = startingLifeState.selectedItem,
                            titleLabel = stringResource(R.string.select_life_label),
                            modifier = Modifier.fillMaxWidth(),
                            onItemSelected = onLifeTotalSelected
                        )
                    }
                }

                item {
                    HomeCardSetSelector(
                        availableSetsState = availableSetsState,
                        onSetSelected = onSetSelected,
                        onDownloadSchemesClick = onDownloadSchemesClick,
                        onNavigateToSchemeScreen = onNavigateToSchemeScreen
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            HorizontalDivider(modifier = Modifier)

            HomeSubmitButton(
                selectedPlayerCount = playerCountState.selectedItem,
                selectedStartingLife = startingLifeState.selectedItem,
                selectedSet = availableSetsState.selectedItem,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                onSubmitClicked = onSubmitClicked
            )
        }
    }
}


@Composable
private fun HomeCardSetSelector(
    availableSetsState: HomeItemState<CardSet?>,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onNavigateToSchemeScreen: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownMenu(
            items = availableSetsState.items,
            selectedItem = availableSetsState.selectedItem,
            titleLabel = stringResource(R.string.select_set_label),
            fieldLabel = { set ->
                set?.let { "${set.name} (${set.id.uppercase()})" }
            },
            onItemSelected = onSetSelected
        )

        when (availableSetsState.buttonState) {
            HomeItemButtonState.NotLoaded -> HomeItemStateIcon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.download_schemes),
                onClick = onDownloadSchemesClick
            )

            HomeItemButtonState.Loading -> CircularProgressIndicator(
                modifier = Modifier.padding(start = 16.dp),
            )

            HomeItemButtonState.Available -> HomeItemStateIcon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = stringResource(R.string.navigate_schemes),
                onClick = onNavigateToSchemeScreen
            )

            else -> HomeItemStateIcon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.download_schemes_error),
                onClick = onDownloadSchemesClick
            )
        }
    }
}

@Composable
private fun HomeItemStateIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(start = 8.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun HomeSubmitButton(
    selectedPlayerCount: Int?,
    selectedStartingLife: Int,
    selectedSet: CardSet?,
    modifier: Modifier = Modifier,
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> }
) {
    Button(
        onClick = {
            onSubmitClicked(
                selectedPlayerCount?.let { count ->
                    List(count) { selectedStartingLife }
                },
                selectedSet?.id
            )
        },
        modifier = modifier,
        enabled = selectedPlayerCount != null || selectedSet != null
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
            ),
            isRefreshing = false
        )
    }
}
