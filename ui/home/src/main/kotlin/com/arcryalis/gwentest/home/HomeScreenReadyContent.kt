package com.arcryalis.gwentest.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcryalis.gwentest.core.DropdownMenu
import com.arcryalis.gwentest.data.card.model.CardSet


@Composable
fun HomeScreenReadyContent(
    players: List<String>,
    startingLifeState: HomeItemState<Int>,
    availableSetsState: HomeItemState<CardSet?>,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onLifeTotalSelected: (Int) -> Unit = {},
    onNavigateToSchemeScreen: () -> Unit = {},
    onDownloadSchemesClick: () -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onRemovePlayer: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_players_label),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = onAddPlayer,
                    enabled = players.size < 4
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_player)
                    )
                }
            }
        }

        itemsIndexed(players) { index, playerName ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playerName,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                IconButton(
                    onClick = { onRemovePlayer(index) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove_player, playerName),
                        tint = Color.Red
                    )
                }
            }
        }

        item {
            DropdownMenu(
                items = startingLifeState.items,
                selectedItem = startingLifeState.selectedItem,
                titleLabel = stringResource(R.string.select_life_label),
                modifier = Modifier.fillMaxWidth(),
                onItemSelected = onLifeTotalSelected
            )
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
            modifier = Modifier.weight(1f),
            fieldLabel = { set ->
                set?.let { " ()" }
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
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
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
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(start = 8.dp)
            .clickable(onClick = onClick)
    )
}