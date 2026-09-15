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

    private val ongoingCardList = MutableStateFlow(mutableListOf<CardInfo>())

    private val shuffledCardList = getCardInfoListUseCase(setId).map {
        it.shuffled()
    }

    val state = combine(
        isLoading,
        ongoingCardList,
        shuffledCardList,
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


    fun addCardToOngoingList(card: CardInfo) {
        val cardInList = ongoingCardList.value.contains(card)

        if (!cardInList) {
            ongoingCardList.update {
                ongoingCardList.value.toMutableList().apply {
                    this.add(card)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: SchemeRoute): SchemeViewModel
    }
}