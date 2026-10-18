package com.arcryalis.gwentest.scheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardInfoUseCase
import com.arcryalis.gwentest.core.navigation.SchemeRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = SchemeViewModel.Factory::class)
class SchemeViewModel @AssistedInject constructor(
    @Assisted private val route: SchemeRoute,
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

    @AssistedFactory
    interface Factory {
        fun create(route: SchemeRoute): SchemeViewModel
    }
}