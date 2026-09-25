package com.arcryalis.gwentest.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
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
    private val areCardsAvailableUseCase: AreCardsAvailableUseCase,
    private val downloadSchemesUseCase: DownloadSchemesUseCase,
    private val getCardSetsUseCase: GetCardSetsUseCase,
): ViewModel() {

    companion object {
        private val PLAYER_COUNT_OPTIONS = listOf(1, 2, 3, 4)
        private val LIFE_TOTAL_OPTIONS = listOf(20, 40, 60)
    }

    private val isLoading = MutableStateFlow(true)
    private val hasError = MutableStateFlow(false)

    private val selectedSet = MutableStateFlow<CardSet?>(null)
    val availableSetState = combine(
        getCardSetsUseCase(),
        selectedSet
    ) { availableItems, selected ->
        HomeItemState(
            items = listOf(null) + availableItems, // null for no selection
            selectedItem = selected
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

    val state = combine(
        isLoading,
        hasError,
        availableSetState,
        playerCountState,
        lifeTotalState
    ) { loading, error, setState, playerState, lifeState ->
        when (error) {
            true -> HomeState.Error
            false -> {
                when (loading) {
                    true -> HomeState.Loading
                    false -> {
                        HomeState.Ready(
                            sets = setState,
                            playerCount = playerState,
                            startingLife = lifeState
                        )
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

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
        isLoading.value = true
        hasError.value = false

        val loadSuccessful = downloadSchemesUseCase.invoke()

        hasError.value = !loadSuccessful
        isLoading.value = false
    }

    fun onRefresh() {
        viewModelScope.launch {
            fetchSchemes()
        }
    }

    init {
        viewModelScope.launch {
            val available = areCardsAvailableUseCase()
            if (!available) {
                fetchSchemes()
            } else {
                isLoading.value = false
            }
        }
    }
}