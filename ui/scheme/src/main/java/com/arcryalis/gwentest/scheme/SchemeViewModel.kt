package com.arcryalis.gwentest.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.domain.home.GetCardInfoListUseCase
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = SchemeViewModel.Factory::class)
class SchemeViewModel @AssistedInject constructor(
    @Assisted private val route: SchemeRoute,
    private val getCardInfoListUseCase: GetCardInfoListUseCase
): ViewModel()  {

    private val setId = route.setId

    private val isLoading = MutableStateFlow(false)

    private val ongoingCardList = MutableStateFlow(mutableListOf<CardUiInfo>())

    private val flippedCardList = MutableStateFlow(mutableListOf<CardUiInfo>())

    private val overlayCard = MutableStateFlow<CardUiInfo?>(null)

    private val shuffledCardList = getCardInfoListUseCase(setId).map {
        it.shuffled()
    }

    private val cardList = combine(
        shuffledCardList,
        flippedCardList
    ) { cardList, selectedCardIds ->
        cardList
            .map { card ->
                CardUiInfo(
                    name = card.name,
                    oracleText = card.oracleText,
                    images = card.images,
                    isOngoing = card.isOngoing
                )
            }
    }

    val state = combine(
        isLoading,
        ongoingCardList,
        flippedCardList,
        cardList,
        overlayCard
    ) { loading, ongoingList, flippedCards, shuffledList, overlay ->
        if (loading) {
            SchemeState.Loading
        } else {
            SchemeState.Ready(
                cards = shuffledList,
                faceUpCards = flippedCards,
                ongoingCards = ongoingList,
                overlayCard = overlay
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SchemeState.Loading)

    fun onSchemeClicked(card: CardUiInfo) {
        // automatically add unique ongoing schemes
        val cardInOngoingList = ongoingCardList.value.contains(card)
        val cardInFlippedList = flippedCardList.value.contains(card)
        if (!cardInOngoingList && card.isOngoing && !cardInFlippedList) {
            ongoingCardList.update {
                ongoingCardList.value.toMutableList().apply {
                    this.add(card)
                }
            }
        }

        val cardIsSelected = flippedCardList.value.contains(card)
        if (!cardIsSelected) {
            // flip card
            flippedCardList.update {
                flippedCardList.value.toMutableList().apply {
                    this.add(card)
                }
            }
        } else {
            // show popup
            overlayCard.value = card
        }
    }

    fun onOngoingClicked(card: CardUiInfo) {
        overlayCard.value = card
    }

    fun onCloseOverlayClicked() {
        overlayCard.value = null
    }

    fun onAddOngoingClicked(card: CardUiInfo) {
        onCloseOverlayClicked()

        ongoingCardList.update {
            ongoingCardList.value.toMutableList().apply {
                this.add(card)
            }
        }
    }

    fun onRemoveOngoingClicked(card: CardUiInfo) {
        onCloseOverlayClicked()

        ongoingCardList.update {
            ongoingCardList.value.toMutableList().apply {
                this.remove(card)
            }
        }
    }

    private fun List<CardUiInfo>.containsSameName(name: String): Boolean = this.any {
        it.name == name
    }

    @AssistedFactory
    interface Factory {
        fun create(route: SchemeRoute): SchemeViewModel
    }
}