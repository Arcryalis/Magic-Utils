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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadSchemesUseCase: DownloadSchemesUseCase,
    private val getCardSetsUseCase: GetCardSetsUseCase,
): ViewModel() {

    companion object {
        private val PLAYER_COUNT_OPTIONS = listOf(1, 2, 3, 4)
        private val LIFE_TOTAL_OPTIONS = listOf(20, 40, 60)
    }

    private val selectedSet = MutableStateFlow<CardSet?>(null)
    private val cardSetsLoading = MutableStateFlow(false)
    private val cardSetsError = MutableStateFlow(false)
    val availableSetState = combine(
        getCardSetsUseCase(),
        selectedSet,
        cardSetsLoading,
        cardSetsError
    ) { availableItems, selected, cardsLoading, error ->
        HomeItemState(
            items = listOf(null) + availableItems, // null for no selection
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

    private val selectedPlayerCount = MutableStateFlow<Int?>(null)
    val playerCountState = selectedPlayerCount.map {
        HomeItemState(
            items = listOf(null) + PLAYER_COUNT_OPTIONS, // null for no selection
            selectedItem = it
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
        playerCountState,
        lifeTotalState
    ) { loading, setState, playerState, lifeState ->
        HomeState.Ready(
            sets = setState,
            playerCount = playerState,
            startingLife = lifeState,
            isRefreshing = loading
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Initial)

    fun onSelectSet(set: CardSet?) {
        selectedSet.value = set
    }

    fun onSelectPlayerCount(count: Int?) {
        selectedPlayerCount.value = count
    }

    fun onSelectLifeTotal(life: Int) {
        selectedLifeTotal.value = life
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
            viewModelScope.launch {
                fetchSchemes()
            }
        }
    }

    fun onRefresh() {
        if (!isAnyLoading.value) {
            viewModelScope.launch {
                fetchSchemes()
            }
        }
    }
}
