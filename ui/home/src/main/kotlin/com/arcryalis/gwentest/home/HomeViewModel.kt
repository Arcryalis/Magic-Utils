package com.arcryalis.gwentest.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.GetCardSetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadSchemesUseCase: DownloadSchemesUseCase,
    private val getCardSetsUseCase: GetCardSetsUseCase,
): ViewModel() {

    companion object {
        private const val MAX_PLAYERS = 4
        private val LIFE_TOTAL_OPTIONS = listOf(20, 40, 60)
    }

    private val selectedSet = MutableStateFlow<CardSet?>(null)
    private val cardSetsLoading = MutableStateFlow(false)
    private val cardSetsError = MutableStateFlow(false)
    private val playersList = MutableStateFlow<List<String>>(emptyList())

    val availableSetState = combine(
        getCardSetsUseCase(),
        selectedSet,
        cardSetsLoading,
        cardSetsError
    ) { availableItems, selected, cardsLoading, error ->
        HomeItemState(
            items = listOf(null) + availableItems,
            selectedItem = selected,
            buttonState = if (error) {
                HomeItemButtonState.Error
            } else if (availableItems.isEmpty()) {
                HomeItemButtonState.NotLoaded
            } else if (cardsLoading) {
                HomeItemButtonState.Loading
            } else {
                HomeItemButtonState.Available
            }
        )
    }

    private val selectedLifeTotal = MutableStateFlow(20)
    val lifeTotalState = selectedLifeTotal.map {
        HomeItemState(
            items = LIFE_TOTAL_OPTIONS,
            selectedItem = it
        )
    }

    val isAnyLoading = cardSetsLoading.map { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val state = combine(
        isAnyLoading,
        availableSetState,
        playersList,
        lifeTotalState
    ) { loading, setState, players, lifeState ->
        HomeState.Ready(
            sets = setState,
            players = players,
            startingLife = lifeState,
            isRefreshing = loading
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Initial)

    fun onSelectSet(set: CardSet?) {
        selectedSet.value = set
    }

    fun onSelectLifeTotal(life: Int) {
        selectedLifeTotal.value = life
    }

    fun addPlayer() {
        if (playersList.value.size < MAX_PLAYERS) {
            playersList.update { _ ->
                playersList.value
                    .toMutableList()
                    .apply {
                        this.add("Player ${playersList.value.size + 1}")
                    }.toList()
            }
        }
    }

    fun removePlayer(index: Int) {
        if (index >= 0 && index < playersList.value.size) {
            playersList.update {
                playersList.value
                    .toMutableList()
                    .apply {
                        this.removeAt(index)
                    }.toList()
            }
        }
    }

    private suspend fun fetchSchemes() {
        cardSetsLoading.value = true
        cardSetsError.value = false
        val loadSuccessful = downloadSchemesUseCase()
        cardSetsError.value = !loadSuccessful
        cardSetsLoading.value = false
    }

    fun onDownloadSchemes() {
        if (!cardSetsLoading.value) {
            viewModelScope.launch { fetchSchemes() }
        }
    }

    fun onRefresh() {
        if (!isAnyLoading.value) {
            viewModelScope.launch { fetchSchemes() }
        }
    }
}
