package com.arcryalis.gwentest.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetCardBackUrl
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
    private val getShuffledCardInfoUseCase: GetShuffledCardInfoUseCase,
    private val getCardBackUrl: GetCardBackUrl
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
    private val startingDeck = (
            setId?.let { getShuffledCardInfoUseCase(setId) } ?: flowOf(emptyList())
    ).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var revealedCardIndex = MutableStateFlow<Int?>(null)
    private val revealedCardList = combine(
        startingDeck,
        revealedCardIndex,
    ) { deck, index ->
        if (index != null) {
            deck.take(index+1).reversed()
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val extraCardList = MutableStateFlow<List<CardInfo>>(emptyList())
    private val cardbackUrl = MutableStateFlow<String?>(null)
    private val deckState = combine(
        startingDeck,
        revealedCardList,
        extraCardList,
        cardbackUrl,
    ) { starting, revealedCards, extraCards, backUrl ->
        if (starting.isNotEmpty() && backUrl != null) {
            DeckSettings(
                nextCardUrl = if (revealedCards.size < starting.size) {
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
                val card = overlayCards.firstOrNull()
                if (card != null) {
                    OverlayState.Visible.Individual(
                        info = card,
                        showRemove = extraList.contains(card)
                    )
                } else {
                    OverlayState.Hidden
                }
            }
            else -> OverlayState.Visible.Gallery(overlayCards)
        }
    }

    private val isLoading = MutableStateFlow(true)

    val state = combine(
        playerInfo,
        deckState,
        overlayState,
        isLoading
    ) { players, deck, overlay, loading ->
        if (loading) {
            GameState.Loading
        } else {
            GameState.Ready(
                players = players,
                deckSettings = deck,
                overlayState = overlay
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GameState.Loading)

    init {
        viewModelScope.launch {
            isLoading.value = true
            cardbackUrl.value = getCardBackUrl()
            isLoading.value = false
        }
    }

    fun increasePlayerLife(index: Int) {
        val currentLife = lifeTotals.value?.get(index)
        if (currentLife != null && currentLife < MAX_LIFE) {
            lifeTotals.update {
                lifeTotals.value
                    ?.toMutableList()
                    ?.apply {
                        this[index] = this[index] + 1
                    }?.toList()
            }
        }
    }

    fun decreasePlayerLife(index: Int) {
        val currentLife = lifeTotals.value?.get(index)
        if (currentLife != null && currentLife > MIN_LIFE) {
        lifeTotals.update {
            lifeTotals.value
                ?.toMutableList()
                ?.apply {
                    this[index] = this[index] - 1
                }?.toList()
        }
            }
    }

    fun revealNextCard() {
        val currentIndex = revealedCardIndex.value
        val maxIndex = startingDeck.value.size - 1
        val newIndex = (currentIndex?.let { it + 1 } ?: 0).coerceAtMost(maxIndex)
        revealedCardIndex.value = newIndex

        if (currentIndex != newIndex) {
            val card = startingDeck.value[newIndex]
            if (card.isOngoing) {
                addCardToExtras(card = card)
            }

            showCardOnOverlay(card)
        }
    }

    fun addCardToExtras(card: CardInfo) {
        extraCardList.update { currentList ->
            if (!currentList.contains(card)) {
                currentList + card
            } else {
                currentList
            }
        }
        hideOverlay()
    }

    fun removeCardFromExtras(card: CardInfo) {
        extraCardList.update { currentList ->
            currentList - card
        }
        hideOverlay()
    }

    fun showCardOnOverlay(cardInfo: CardInfo) {
        overlayCards.value = listOf(cardInfo)
    }

    fun showRevealedCardsGallery() {
        viewModelScope.launch {
            overlayCards.value = revealedCardList.first()
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