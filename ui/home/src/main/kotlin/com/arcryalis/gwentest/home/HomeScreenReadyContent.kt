package com.arcryalis.gwentest.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    onRemovePlayer: (Int) -> Unit = {},
    onPlayerNameChange: (Int, String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
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
                    enabled = players.size < 4,
                    modifier = Modifier.width(36.dp)
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val label = stringResource(R.string.player_name_label, index + 1)

                OutlinedTextField(
                    value = playerName,
                    onValueChange = { newName -> onPlayerNameChange(index, newName) },
                    label = {
                        Text(
                            text = label,
                            modifier = Modifier.alpha(if (playerName.isEmpty()) 0.5f else 1f)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onRemovePlayer(index) },
                    modifier = Modifier.width(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove_player, playerName),
                        tint = Color.Red
                    )
                }
            }
        }

        if (players.isNotEmpty()) {
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DropdownMenu(
            items = availableSetsState.items,
            selectedItem = availableSetsState.selectedItem,
            titleLabel = stringResource(R.string.select_set_label),
            modifier = Modifier.weight(1f),
            fieldLabel = { set ->
                set?.let { "${it.name} (${it.id.uppercase()} )" }
            },
            onItemSelected = onSetSelected
        )

        Box(
            modifier = Modifier.width(36.dp)
        ) {
            when (availableSetsState.buttonState) {
                HomeItemButtonState.NotLoaded -> HomeItemStateIcon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.download_schemes),
                    onClick = onDownloadSchemesClick,
                )

                HomeItemButtonState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
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
}

@Composable
private fun HomeItemStateIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}