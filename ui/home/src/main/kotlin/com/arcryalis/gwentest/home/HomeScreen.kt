package com.arcryalis.gwentest.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.data.card.model.CardSet

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSchemeScreen: (String) -> Unit = {},
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> }
) {
    val state = viewModel.state.collectAsState()
    HomeScreen(
        state = state.value,
        modifier = modifier,
        onNavigateToGameScreen = onNavigateToGameScreen,
        onSetSelected = viewModel::onSelectSet,
        onRefreshClick = viewModel::onRefresh
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToGameScreen: (List<Int>?, String?) -> Unit = { _, _ -> },
    onSetSelected: (CardSet?) -> Unit = {},
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
            availableSets = state.availableSets,
            selectedSet = state.selectedSet,
            modifier = modifier,
            onSetSelected = onSetSelected,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeReadyScreen(
    availableSets: List<CardSet>,
    selectedSet: CardSet?,
    modifier: Modifier = Modifier,
    onSetSelected: (CardSet?) -> Unit = {},
    onSubmitClicked: (List<Int>?, String?) -> Unit = { _, _ -> },
    onRefresh: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val emptyText = stringResource(R.string.empty_option)

    PullToRefreshBox(
        isRefreshing = false, //handled by HomeState instead
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedSet?.let { "${it.name} (${it.id.uppercase()})" } ?: emptyText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select_set_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = emptyText,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                onSetSelected(null)
                                expanded = false
                            }
                        )
                        availableSets.forEach { set ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${set.name} (${set.id.uppercase()})",
                                        fontSize = 16.sp
                                    )
                                },
                                onClick = {
                                    onSetSelected(set)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmitClicked(
                            listOf(40, 40),
                            selectedSet?.id
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
        val sets = listOf(
            CardSet("dci", "DCI Promos"),
            CardSet("oarc", "Archenemy Schemes"),
            CardSet("oe01", "Archenemy: Nicol Bolas Schemes")
        )
        HomeReadyScreen(
            availableSets = sets,
            selectedSet = sets.firstOrNull()
        )
    }
}