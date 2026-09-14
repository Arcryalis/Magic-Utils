package com.arcryalis.gwentest.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.CardInfo
import com.arcryalis.gwentest.home.GetCardInfoListUseCase
import com.arcryalis.gwentest.home.TestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val testUseCase: TestUseCase,
    private val getCardInfoListUseCase: GetCardInfoListUseCase
): ViewModel()  {
    private val currentName = MutableStateFlow<String>("")

    private val isLoading = MutableStateFlow<Boolean>(false)

    val state = combine(
        getCardInfoListUseCase(),
        isLoading
    ) { cardList, loading ->
        if (loading) {
            emptyList<CardInfo>()
        } else {
            Log.i("HomeViewModel", "state: $cardList")
            cardList //TODO add model
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<CardInfo>())

    private suspend fun fetchName() {
        isLoading.value = true
        currentName.value = testUseCase.invoke()
        isLoading.value = false
    }

    init {
        viewModelScope.launch {
            fetchName()
        }
    }
}