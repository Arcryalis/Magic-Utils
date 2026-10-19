package com.arcryalis.gwentest.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetCardBackUrlUseCase
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.core.navigation.GameRoute
import com.arcryalis.gwentest.game.mapper.toModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GameViewModel.Factory::class)
class GameViewModel @AssistedInject constructor(
    @Assisted private val route: GameRoute,
    private val getShuffledCardInfoUseCase: GetShuffledCardInfoUseCase,
    private val getCardBackUrl: GetCardBackUrlUseCase
): ViewModel()  {

    companion object {
        private const val MAX_LIFE = 999
        private const val MIN_LIFE = -99
    }

    private val initialPlayerSettings = route.playerSettings
    private val playerInfo: MutableStateFlow<List<PlayerInfo>?> = MutableStateFlow(initialPlayerSettings?.toModel())

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
    private val hasError = MutableStateFlow(false)
    private val deckState = combine(
        startingDeck,
        revealedCardList,
        extraCardList,
        cardbackUrl,
        hasError,
    ) { starting, revealedCards, extraCards, backUrl, error ->
        if (!error && starting.isNotEmpty() && backUrl != null) {
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

    private val overlayRequest = MutableStateFlow<OverlayRequest>(OverlayRequest.None)
    private val overlayState = combine(
        overlayRequest,
        extraCardList
    ) { request, extraList ->
        when (request) {
            OverlayRequest.None -> OverlayState.Hidden
            is OverlayRequest.Card -> OverlayState.Visible.Individual(
                info = request.card,
                showRemove = extraList.contains(request.card)
            )
            is OverlayRequest.Gallery -> OverlayState.Visible.Gallery(request.cards)
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
            try {
                cardbackUrl.value = getCardBackUrl()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                hasError.value = true
            } finally {
                isLoading.value = false
            }
        }
    }

    fun increasePlayerLife(index: Int) {
        val currentLife = playerInfo.value?.get(index)?.lifeTotal
        if (currentLife != null && currentLife < MAX_LIFE) {
            playerInfo.update {
                playerInfo.value
                    ?.toMutableList()
                    ?.apply {
                        this[index] = this[index].copy(lifeTotal = currentLife + 1)
                    }?.toList()
            }
        }
    }

    fun decreasePlayerLife(index: Int) {
        val currentLife = playerInfo.value?.get(index)?.lifeTotal
        if (currentLife != null && currentLife > MIN_LIFE) {
            playerInfo.update {
                playerInfo.value
                ?.toMutableList()
                ?.apply {
                    this[index] = this[index].copy(lifeTotal = currentLife - 1)
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
        overlayRequest.value = OverlayRequest.Card(cardInfo)
    }

    fun showRevealedCardsGallery() {
        val revealed = revealedCardList.value
        overlayRequest.value = when (revealed.size) {
            0 -> OverlayRequest.None
            1 -> OverlayRequest.Card(revealed.first())
            else -> OverlayRequest.Gallery(revealed)
        }
    }

    fun hideOverlay() {
        overlayRequest.value = OverlayRequest.None
    }

    @AssistedFactory
    interface Factory {
        fun create(route: GameRoute): GameViewModel
    }
}

private sealed interface OverlayRequest {
    data object None : OverlayRequest
    data class Card(val card: CardInfo) : OverlayRequest
    data class Gallery(val cards: List<CardInfo>) : OverlayRequest
}