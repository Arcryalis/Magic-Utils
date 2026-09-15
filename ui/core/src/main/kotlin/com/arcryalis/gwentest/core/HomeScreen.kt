package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
        onNavigateToSchemeScreen = onNavigateToSchemeScreen
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    onNavigateToSchemeScreen: (String) -> Unit = {}
) {
    when (state) {
        is HomeState.Loading -> LoadingScreen(modifier)
        is HomeState.Ready -> ReadyScreen(
            availableSets = state.availableSets,
            modifier = modifier,
            onItemClick = onNavigateToSchemeScreen
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ReadyScreen(
    availableSets: List<CardSet>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {}
) {

    LazyColumn(modifier) {
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