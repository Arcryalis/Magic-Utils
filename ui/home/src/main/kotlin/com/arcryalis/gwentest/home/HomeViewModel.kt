package com.arcryalis.gwentest.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.domain.home.AreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.home.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.home.GetCardSetsUseCase
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
): ViewModel()  {

    private val isLoading = MutableStateFlow(true)

    private val hasError = MutableStateFlow(false)

    val state = combine(
        isLoading,
        hasError,
        getCardSetsUseCase()
    ) { loading, error, availableSets ->
        when (error) {
            true -> HomeState.Error
            false -> {
                when (loading) {
                    true -> HomeState.Loading
                    false -> HomeState.Ready(availableSets)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

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