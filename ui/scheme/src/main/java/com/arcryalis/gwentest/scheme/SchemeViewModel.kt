package com.arcryalis.gwentest.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SchemeViewModel @Inject constructor(
    private val getAllCardInfoUseCase: GetAllCardInfoUseCase
): ViewModel()  {

    private val overlayCard = MutableStateFlow<CardInfo?>(null)

    val state = combine(
        getAllCardInfoUseCase(),
        overlayCard
    ) { allCards, overlay ->
        if (allCards.isEmpty()) {
            SchemeState.Loading
        } else {
            SchemeState.Ready(
                cards = allCards,
                overlayCard = overlay
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SchemeState.Loading)

    fun onCloseOverlayClicked() {
        overlayCard.value = null
    }

    fun onCardClicked(card: CardInfo) {
        overlayCard.value = card
    }
}