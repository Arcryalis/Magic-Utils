package com.arcryalis.gwentest.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.home.DownloadSchemesUseCase
import com.arcryalis.gwentest.home.GetCardSetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadSchemesUseCase: DownloadSchemesUseCase,
    private val getCardSetsUseCase: GetCardSetsUseCase,
): ViewModel()  {

    private val isLoading = MutableStateFlow<Boolean>(true)

    val state = combine(
        isLoading,
        getCardSetsUseCase()
    ) { loading, availableSets ->
        when (loading) {
            true -> HomeState.Loading
            false -> HomeState.Ready(availableSets)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState.Loading)

    private suspend fun fetchName() {
        isLoading.value = true
        //TODO handle for more info
        //val loadSuccessful =
        downloadSchemesUseCase.invoke()
        isLoading.value = false
    }

    init {
        viewModelScope.launch {
            //TODO default don't fetch if DB has values, add refresh to force
            fetchName()
        }
    }
}