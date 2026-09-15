package com.arcryalis.gwentest.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import com.arcryalis.gwentest.home.GetCardInfoListUseCase
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

    private val selectedCardIds = MutableStateFlow(mutableListOf<CardUiInfo>())

    private val shuffledCardList = getCardInfoListUseCase(setId).map {
        it.shuffled()
    }

    private val cardList = combine(
        shuffledCardList,
        selectedCardIds
    ) { cardList, selectedCardIds ->
        cardList
            .map { card ->
                CardUiInfo(
                    name = card.name,
                    images = card.images,
                    isOngoing = card.isOngoing,
                    isFaceUp = selectedCardIds.containsSameName(card.name)
                )
            }
    }

    val state = combine(
        isLoading,
        ongoingCardList,
        cardList,
    ) { loading, ongoingList, shuffledList ->
        if (loading) {
            SchemeState.Loading
        } else {
            SchemeState.Ready(
                cards = shuffledList,
                ongoingCards = ongoingList
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SchemeState.Loading)

    fun onSchemeClicked(card: CardUiInfo) {
        val cardInOngoingList = ongoingCardList.value.containsSameName(card.name)
        if (!cardInOngoingList && card.isOngoing && !card.isFaceUp) {
            ongoingCardList.update {
                ongoingCardList.value.toMutableList().apply {
                    this.add(card)
                }
            }
        }

        val cardIsSelected = selectedCardIds.value.containsSameName(card.name)
        if (!cardIsSelected) {
            selectedCardIds.update {
                selectedCardIds.value.toMutableList().apply {
                    this.add(card)
                }
            }
        } else {
            selectedCardIds.update {
                selectedCardIds.value.toMutableList().apply {
                    this.removeAll {
                        it.name == card.name
                    }
                }
            }
        }
    }

    fun onOngoingClicked(card: CardUiInfo) {
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