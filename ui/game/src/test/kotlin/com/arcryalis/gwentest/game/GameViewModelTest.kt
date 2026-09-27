package com.arcryalis.gwentest.game

import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetCardBackUrlUseCase
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardBackUrlUseCase
import com.arcryalis.gwentest.domain.card.MockGetShuffledCardInfoUseCase
import com.arcryalis.gwentest.game.navigation.GameRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    companion object {
        const val DEFAULT_SET_ID = "defaultSet"
    }

    @get:Rule
    val mainDispatcher: BaseDispatchRule = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var route: GameRoute
    private lateinit var mockGetShuffledCardInfoUseCase: GetShuffledCardInfoUseCase
    private lateinit var mockGetCardBackUrlUseCase: GetCardBackUrlUseCase
    private lateinit var sut: GameViewModel

    fun setUpSut(
        lifeTotals: List<Int>? = listOf(20, 20),
        setId: String = DEFAULT_SET_ID,
        shuffledCards: Map<String, List<CardInfo>> = mapOf(
            setId to listOf(
                TestCardInfo.info1,
                 TestCardInfo.info2
            )
        )
    ) {
        route = GameRoute(
            playerLifeTotals = lifeTotals,
            setId = setId,
        )

        mockGetShuffledCardInfoUseCase = MockGetShuffledCardInfoUseCase(shuffledCards)
        mockGetCardBackUrlUseCase = MockGetCardBackUrlUseCase("cardbackUrl")

        sut = GameViewModel(
            route = route,
            getShuffledCardInfoUseCase = mockGetShuffledCardInfoUseCase,
            getCardBackUrl = mockGetCardBackUrlUseCase,
        )
    }

    @Test
    fun whenInitialized_thenStateIsReadyWithInitialPlayersAndDeck() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val state = sut.state.value
        assertIs<GameState.Ready>(state)

        assertEquals(2, state.players?.size)
        assertEquals("20", state.players?.get(0)?.lifeTotal)
        assertEquals("20", state.players?.get(1)?.lifeTotal)

        assertNotNull(state.deckSettings)
        assertEquals(emptyList(), state.deckSettings.revealedCards)
        assertNotNull(state.deckSettings.nextCardUrl)
        assertEquals(emptyList(), state.deckSettings.extraCardList)

        assertIs<OverlayState.Hidden>(state.overlayState)
    }

    @Test
    fun whenIncreaseAndDecreasePlayerLife_thenLifeTotalsUpdate() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.increasePlayerLife(0)
        var state = sut.state.value as GameState.Ready
        assertEquals("21", state.players?.get(0)?.lifeTotal)

        sut.decreasePlayerLife(1)
        state = sut.state.value as GameState.Ready
        assertEquals("19", state.players?.get(1)?.lifeTotal)
    }

    @Test
    fun givenLifeAtMax_whenIncreasePlayerLife_thenLifeStaysAtMax() {
        setUpSut(
            lifeTotals = listOf(999)
        )
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.increasePlayerLife(0)
        val state = sut.state.value as GameState.Ready
        assertEquals("999", state.players?.get(0)?.lifeTotal)
    }

    @Test
    fun givenLifeAtMin_whenDecreasePlayerLife_thenLifeStaysAtMin() {
        setUpSut(
            lifeTotals = listOf(-99)
        )
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.decreasePlayerLife(0)
        val state = sut.state.value as GameState.Ready
        assertEquals("-99", state.players?.get(0)?.lifeTotal)
    }

    @Test
    fun whenRevealNextCard_nonOngoingCard_thenCardRevealedAndShownOnOverlay() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.revealNextCard()

        val state = sut.state.value as GameState.Ready
        val deckSettings = state.deckSettings
        assertNotNull(deckSettings)
        assertEquals(1, deckSettings.revealedCards?.size)
        assertEquals(TestCardInfo.info1, deckSettings.revealedCards?.first())
        assertEquals(emptyList(), deckSettings.extraCardList)

        val overlay = state.overlayState
        assertIs<OverlayState.Visible.Individual>(overlay)
        assertEquals(TestCardInfo.info1, overlay.info)
        assertEquals(expected = false, actual = overlay.showRemove)
    }

    @Test
    fun whenRevealNextCard_ongoingCard_thenAddedToExtrasAndShownOnOverlay() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        // Reveal first card (info1)
        sut.revealNextCard()
        // Reveal second card (info2 - ongoing)
        sut.revealNextCard()

        val state = sut.state.value as GameState.Ready
        val deckSettings = state.deckSettings
        assertNotNull(deckSettings)
        assertEquals(2, deckSettings.revealedCards?.size)
        assertEquals(listOf(TestCardInfo.info2, TestCardInfo.info1), deckSettings.revealedCards)
        assertEquals(listOf(TestCardInfo.info2), deckSettings.extraCardList)

        val overlay = state.overlayState
        assertIs<OverlayState.Visible.Individual>(overlay)
        assertEquals(TestCardInfo.info2, overlay.info)
        assertEquals(expected = true, actual = overlay.showRemove)
    }

    @Test
    fun whenAddAndRemoveCardFromExtras_thenUpdatesExtraList() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addCardToExtras(TestCardInfo.info1)
        var state = sut.state.value as GameState.Ready
        assertEquals(listOf(TestCardInfo.info1), state.deckSettings?.extraCardList)
        assertIs<OverlayState.Hidden>(state.overlayState)

        sut.removeCardFromExtras(TestCardInfo.info1)
        state = sut.state.value as GameState.Ready
        assertEquals(emptyList(), state.deckSettings?.extraCardList)
        assertIs<OverlayState.Hidden>(state.overlayState)
    }

    @Test
    fun givenCardAlreadyInExtras_whenAddCardToExtras_thenDoesNotDuplicate() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addCardToExtras(TestCardInfo.info1)
        var state = sut.state.value as GameState.Ready
        assertEquals(listOf(TestCardInfo.info1), state.deckSettings?.extraCardList)

        sut.addCardToExtras(TestCardInfo.info1)
        state = sut.state.value as GameState.Ready
        assertEquals(listOf(TestCardInfo.info1), state.deckSettings?.extraCardList)
    }

    @Test
    fun whenShowCardOnOverlay_thenOverlayBecomesVisibleIndividual() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.showCardOnOverlay(TestCardInfo.info1)

        val state = sut.state.value as GameState.Ready
        val overlay = state.overlayState
        assertIs<OverlayState.Visible.Individual>(overlay)
        assertEquals(TestCardInfo.info1, overlay.info)
    }

    @Test
    fun givenOneCardInGallery_whenShowRevealedCardsGallery_thenOverlayIndividual() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.revealNextCard()
        sut.showRevealedCardsGallery()

        val state = sut.state.value as GameState.Ready
        val overlay = state.overlayState
        assertIs<OverlayState.Visible.Individual>(overlay)
        assertEquals(false, overlay.showRemove)
        assertEquals(TestCardInfo.info1, overlay.info)
    }

    @Test
    fun whenRevealMultipleCardsAndShowGallery_thenOverlayBecomesVisibleGalleryWithMultipleCards() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.revealNextCard()
        sut.revealNextCard()
        sut.showRevealedCardsGallery()

        val state = sut.state.value as GameState.Ready
        val overlay = state.overlayState
        assertIs<OverlayState.Visible.Gallery>(overlay)
        assertEquals(2, overlay.cards.size)
        assertEquals(listOf(TestCardInfo.info2, TestCardInfo.info1), overlay.cards)
    }

    @Test
    fun whenHideOverlay_thenOverlayBecomesHidden() {
        setUpSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.showCardOnOverlay(TestCardInfo.info1)
        sut.hideOverlay()

        val state = sut.state.value as GameState.Ready
        assertIs<OverlayState.Hidden>(state.overlayState)
    }
}
