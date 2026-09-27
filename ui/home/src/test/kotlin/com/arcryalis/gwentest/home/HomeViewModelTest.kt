package com.arcryalis.gwentest.home

import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.data.card.TestCardSet
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardSetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private class TestDownloadSchemesUseCase(
        var downloadResult: Boolean = true,
    ) : DownloadSchemesUseCase {
        var invokeCount = 0
            private set
        var shouldSuspend = false

        override suspend fun invoke(): Boolean {
            invokeCount++
            if (shouldSuspend) {
                delay(1.seconds)
            }
            return downloadResult
        }
    }

    private lateinit var testDownloadSchemesUseCase: TestDownloadSchemesUseCase
    private lateinit var sut: HomeViewModel

    private fun createSut(
        downloadResult: Boolean = true,
        cardSets: List<CardSet> = listOf(
            TestCardSet.set1,
            TestCardSet.set2,
        ),
    ): HomeViewModel {
        testDownloadSchemesUseCase = TestDownloadSchemesUseCase(downloadResult = downloadResult)
        return HomeViewModel(
            downloadSchemesUseCase = testDownloadSchemesUseCase,
            getCardSetsUseCase = MockGetCardSetsUseCase(cardSets = cardSets),
        )
    }

    @Test
    fun givenCardsAvailableLocally_whenInitialized_thenStateIsReadyAndSchemesNotDownloaded() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)

        assertEquals(0, testDownloadSchemesUseCase.invokeCount)
        assertEquals(listOf(null, TestCardSet.set1, TestCardSet.set2), currentState.sets.items)
        assertEquals(null, currentState.sets.selectedItem)
        assertEquals(HomeItemButtonState.Available, currentState.sets.buttonState)

        assertEquals(listOf(null, 1, 2, 3, 4), currentState.playerCount.items)
        assertEquals(null, currentState.playerCount.selectedItem)

        assertEquals(listOf(20, 40, 60), currentState.startingLife.items)
        assertEquals(20, currentState.startingLife.selectedItem)
    }

    @Test
    fun givenEmptyCardSets_whenInitialized_thenButtonStateIsNotLoaded() {
        sut = createSut(cardSets = emptyList())
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)
        assertEquals(HomeItemButtonState.NotLoaded, currentState.sets.buttonState)
    }

    @Test
    fun givenDownloadSucceeds_whenInitializedWithEmptyCards_thenDownloadsSchemesAndStateIsReady() {
        sut = createSut(cardSets = emptyList(), downloadResult = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onDownloadSchemes()

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun givenDownloadFails_whenDownloadSchemes_thenButtonStateIsError() {
        sut = createSut(cardSets = emptyList(), downloadResult = false)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onDownloadSchemes()

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)
        assertEquals(HomeItemButtonState.Error, currentState.sets.buttonState)
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun givenNotLoading_whenOnDownloadSchemes_thenInvokesDownloadSchemes() {
        sut = createSut(cardSets = emptyList(), downloadResult = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onDownloadSchemes()

        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun givenAlreadyLoading_whenOnDownloadSchemes_thenDoesNotInvokeAgain() = testScope.runTest {
        testDownloadSchemesUseCase = TestDownloadSchemesUseCase(downloadResult = true).apply {
            shouldSuspend = true
        }
        sut = HomeViewModel(
            downloadSchemesUseCase = testDownloadSchemesUseCase,
            getCardSetsUseCase = MockGetCardSetsUseCase(cardSets = emptyList()),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        // Start first download (suspends)
        sut.onDownloadSchemes()
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)

        // Try second download while loading
        sut.onDownloadSchemes()
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun givenNotLoading_whenOnRefresh_thenInvokesDownloadSchemes() {
        sut = createSut(cardSets = listOf(TestCardSet.set1), downloadResult = true)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onRefresh()

        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun givenAlreadyLoading_whenOnRefresh_thenDoesNotInvokeAgain() = testScope.runTest {
        testDownloadSchemesUseCase = TestDownloadSchemesUseCase(downloadResult = true).apply {
            shouldSuspend = true
        }
        sut = HomeViewModel(
            downloadSchemesUseCase = testDownloadSchemesUseCase,
            getCardSetsUseCase = MockGetCardSetsUseCase(cardSets = listOf(TestCardSet.set1)),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        // Start first refresh (suspends)
        sut.onRefresh()
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)

        // Try second refresh while loading
        sut.onRefresh()
        assertEquals(1, testDownloadSchemesUseCase.invokeCount)
    }

    @Test
    fun whenSetSelected_thenUpdatesSetState() {
        sut = createSut()
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
        sut = createSut()
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
        sut = createSut()
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
