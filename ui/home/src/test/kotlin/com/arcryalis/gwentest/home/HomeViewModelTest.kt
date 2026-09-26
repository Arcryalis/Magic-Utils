package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.data.card.TestCardSet
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.MockAreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardSetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private class TestDownloadSchemesUseCase(
        var downloadResult: Boolean = true
    ) : DownloadSchemesUseCase {
        var invokeCount = 0
            private set

        override suspend fun invoke(): Boolean {
            invokeCount++
            return downloadResult
        }
    }

    private lateinit var testDownloadSchemesUseCase: TestDownloadSchemesUseCase
    private lateinit var sut: HomeViewModel

    private fun createSut(
        areCardsAvailable: Boolean = true,
        downloadResult: Boolean = true,
        cardSets: List<CardSet> = listOf(
            TestCardSet.set1,
            TestCardSet.set2
        )
    ): HomeViewModel {
        testDownloadSchemesUseCase = TestDownloadSchemesUseCase(downloadResult = downloadResult)
        return HomeViewModel(
            downloadSchemesUseCase = testDownloadSchemesUseCase,
            getCardSetsUseCase = MockGetCardSetsUseCase(cardSets = cardSets)
        )
    }

    @Test
    fun givenCardsAvailableLocally_whenInitialized_thenStateIsReadyAndSchemesNotDownloaded() {
        sut = createSut(areCardsAvailable = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)

        assertEquals(0, testDownloadSchemesUseCase.invokeCount)
        assertEquals(listOf(null, TestCardSet.set1, TestCardSet.set2), currentState.sets.items)
        assertEquals(null, currentState.sets.selectedItem)

        assertEquals(listOf(null, 1, 2, 3, 4), currentState.playerCount.items)
        assertEquals(null, currentState.playerCount.selectedItem)

        assertEquals(listOf(20, 40, 60), currentState.startingLife.items)
        assertEquals(20, currentState.startingLife.selectedItem)
    }

    @Test
    fun givenCardsNotAvailableLocallyAndDownloadSucceeds_whenInitialized_thenDownloadsSchemesAndStateIsReady() {
        sut = createSut(areCardsAvailable = false, downloadResult = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun whenSetSelected_thenUpdatesSetState() {
        sut = createSut(areCardsAvailable = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val initialState = sut.state.value
        assertIs<HomeState.Ready>(initialState)
        assertEquals(null, initialState.sets.selectedItem)

        sut.onSelectSet(TestCardSet.set1)

        val updatedState = sut.state.value
        assertIs<HomeState.Ready>(updatedState)
        assertEquals(TestCardSet.set1, updatedState.sets.selectedItem)

        sut.onSelectSet(null)

        val finalState = sut.state.value
        assertIs<HomeState.Ready>(finalState)
        assertEquals(null, finalState.sets.selectedItem)
    }

    @Test
    fun whenPlayerCountSelected_thenUpdatesPlayerCountState() {
        sut = createSut(areCardsAvailable = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val initialState = sut.state.value
        assertIs<HomeState.Ready>(initialState)
        assertEquals(null, initialState.playerCount.selectedItem)

        sut.onSelectPlayerCount(3)

        val updatedState = sut.state.value
        assertIs<HomeState.Ready>(updatedState)
        assertEquals(3, updatedState.playerCount.selectedItem)

        sut.onSelectPlayerCount(null)

        val finalState = sut.state.value
        assertIs<HomeState.Ready>(finalState)
        assertEquals(null, finalState.playerCount.selectedItem)
    }

    @Test
    fun whenLifeTotalSelected_thenUpdatesLifeTotalState() {
        sut = createSut(areCardsAvailable = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val initialState = sut.state.value
        assertIs<HomeState.Ready>(initialState)
        assertEquals(20, initialState.startingLife.selectedItem)

        sut.onSelectLifeTotal(40)

        val updatedState = sut.state.value
        assertIs<HomeState.Ready>(updatedState)
        assertEquals(40, updatedState.startingLife.selectedItem)
    }
}
