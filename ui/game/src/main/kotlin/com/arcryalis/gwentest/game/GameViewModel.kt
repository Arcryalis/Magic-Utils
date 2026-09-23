package com.arcryalis.gwentest.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.game.navigation.GameRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GameViewModel.Factory::class)
class GameViewModel @AssistedInject constructor(
    @Assisted private val route: GameRoute,
    private val getShuffledCardInfoUseCase: GetShuffledCardInfoUseCase
): ViewModel()  {

    companion object {
        private const val MAX_LIFE = 999
        private const val MIN_LIFE = -99
    }

    private val startingLifeTotals = route.playerLifeTotals
    private val lifeTotals: MutableStateFlow<List<Int>?> = MutableStateFlow(startingLifeTotals)
    private val playerInfo = lifeTotals.map {
        it?.map { lifeTotal ->
            PlayerInfo(
                lifeTotal = lifeTotal.toString()
            )
        }
    }

    private val setId = route.setId
    private val startingDeck = setId?.let {
        getShuffledCardInfoUseCase(setId)
    } ?: flowOf(null)
    private var revealedCardIndex = MutableStateFlow<Int?>(null)
    private val revealedCardList = combine(
        startingDeck,
        revealedCardIndex,
    ) { deck, index ->
        if (deck != null && index != null) {
            deck.take(index).reversed()
        } else {
            null
        }
    }
    private val extraCardList = MutableStateFlow(mutableListOf<CardInfo>())
    private val cardbackUrl = MutableStateFlow("")
    private val deckState = combine(
        startingDeck,
        revealedCardList,
        extraCardList,
        cardbackUrl,
    ) { starting, revealedCards, extraCards, backUrl ->
        if (starting != null) {
            val totalRevealed = revealedCards?.size ?: 0
            DeckSettings(
                nextCardUrl = if(totalRevealed < starting.size) {
                    backUrl
                } else {
                    null
                },
                revealedCards = revealedCards,
                extraCardList = extraCards
            )
        } else {
            null
        }
    }

    private val overlayCards = MutableStateFlow<List<CardInfo>>(emptyList())
    private val overlayState = combine(
        overlayCards,
        extraCardList
    ) { overlayCards, extraList ->
        when (overlayCards.size) {
            0 -> OverlayState.Hidden
            1 -> {
                val card = overlayCards.first()
                OverlayState.Visible.Individual(
                    info = card,
                    showRemove = extraList.contains(card)
                )
            }
            else -> OverlayState.Visible.Gallery(overlayCards)
        }
    }

    val state = combine(
        playerInfo,
        deckState,
        overlayState
    ) { players, deck, overlay ->
        GameState.Ready(
            players = players,
            deckSettings = deck,
            overlayState = overlay
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GameState.Loading)

    fun increasePlayerLife(index: Int) {
        lifeTotals.update {
            lifeTotals.value
                ?.toMutableList()
                ?.apply {
                    this[index] = this[index] + 1
                }?.toList()
        }
    }

    fun decreasePlayerLife(index: Int) {
//        lifeTotals.update {
//            lifeTotals.value.apply {
//                if (this != null) {
//                    val currentVal = this[index]
//                    this[index] = currentVal - 1
//                }
//            }
//        }
    }

    fun revealNextCard() {
        val currentIndex = revealedCardIndex.value
        val newIndex = currentIndex?.let { it + 1 } ?: 0
        revealedCardIndex.value = newIndex

        viewModelScope.launch {
            val card = revealedCardList.first()?.first()
            if (card != null) {
                showCardOnOverlay(card)

                if(card.isOngoing) {
                    addCardToExtras(card = card)
                }
            }
        }
    }

    fun addCardToExtras(card: CardInfo) {
        extraCardList.update {
            extraCardList.value.apply {
                this.add(card)
            }
        }
    }

    fun removeCardFromExtras(card: CardInfo) {
        extraCardList.update {
            extraCardList.value.apply {
                this.remove(card)
            }
        }
    }

    fun showCardOnOverlay(cardInfo: CardInfo) {
        overlayCards.value = listOf(cardInfo)
    }

    fun showRevealedCardsGallery() {
        viewModelScope.launch {
            overlayCards.value = revealedCardList.first() ?: emptyList()
        }
    }

    fun hideOverlay() {
        overlayCards.value = emptyList()
    }

    @AssistedFactory
    interface Factory {
        fun create(route: GameRoute): GameViewModel
    }
}