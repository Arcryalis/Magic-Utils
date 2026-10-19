package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.MockGetAllCardInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SchemeViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var sut: SchemeViewModel

    private fun setupSut(
        cards: List<CardInfo> = listOf(TestCardInfo.info1, TestCardInfo.info2),
    ) {
        sut = SchemeViewModel(
            getAllCardInfoUseCase = MockGetAllCardInfoUseCase(cardInfo = cards),
        )
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }
    }

    @Test
    fun whenInitializedWithEmptyCards_thenStateIsLoading() {
        setupSut(cards = emptyList())

        val state = sut.state.value
        assertIs<SchemeState.Loading>(state)
    }

    @Test
    fun whenInitializedWithCards_thenStateIsReadyWithCardsAndNullOverlay() {
        setupSut()

        val state = sut.state.value
        assertIs<SchemeState.Ready>(state)
        assertEquals(listOf(TestCardInfo.info1, TestCardInfo.info2), state.cards)
        assertNull(state.overlayCard)
    }

    @Test
    fun whenCardClickedThenOverlayClosed_thenOverlayCardIsSetThenCleared() {
        setupSut()

        sut.onCardClicked(TestCardInfo.info1)
        var state = sut.state.value as SchemeState.Ready
        assertEquals(TestCardInfo.info1, state.overlayCard)

        sut.onCloseOverlayClicked()
        state = sut.state.value as SchemeState.Ready
        assertNull(state.overlayCard)
    }
}
