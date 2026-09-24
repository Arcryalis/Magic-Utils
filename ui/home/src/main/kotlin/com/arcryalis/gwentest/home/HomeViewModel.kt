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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val areCardsAvailableUseCase: AreCardsAvailableUseCase,
    private val downloadSchemesUseCase: DownloadSchemesUseCase,
    private val getCardSetsUseCase: GetCardSetsUseCase,
): ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val hasError = MutableStateFlow(false)
    private val selectedSet = MutableStateFlow<CardSet?>(null)

    val state = combine(
        isLoading,
        hasError,
        getCardSetsUseCase(),
        selectedSet
    ) { loading, error, availableSets, selected ->
        when (error) {
            true -> HomeState.Error
            false -> {
                when (loading) {
                    true -> HomeState.Loading
                    false -> {
                        HomeState.Ready(
                            availableSets = availableSets,
                            selectedSet = selected
                        )
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

    fun onSelectSet(set: CardSet?) {
        selectedSet.value = set
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