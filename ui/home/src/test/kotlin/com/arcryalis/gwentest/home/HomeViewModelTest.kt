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

        assertEquals(emptyList(), currentState.players)

        assertEquals(listOf(20, 40, 60), currentState.startingLife.items)
        assertEquals(20, currentState.startingLife.selectedItem)
    }
    @Test
    fun whenPlayerAdded_thenUpdatesPlayersListWithDefaultNames() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val initialState = sut.state.value
        assertIs<HomeState.Ready>(initialState)
        assertEquals(emptyList(), initialState.players)

        sut.addPlayer()
        assertEquals(listOf("Player 1"), sut.state.value.let { val r = it as HomeState.Ready; r.players })

        sut.addPlayer()
        assertEquals(listOf("Player 1", "Player 2"), sut.state.value.let { val r = it as HomeState.Ready; r.players })
    }

    @Test
    fun whenPlayerAddedAtMax_thenDoesNotAddMore() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.addPlayer()
        sut.addPlayer()
        sut.addPlayer()
        assertEquals(4, (sut.state.value as HomeState.Ready).players.size)

        sut.addPlayer()
        assertEquals(4, (sut.state.value as HomeState.Ready).players.size)
    }

    @Test
    fun whenPlayerRemoved_thenUpdatesPlayersList() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.addPlayer()
        sut.addPlayer()

        sut.removePlayer(1)
        assertEquals(listOf("Player 1", "Player 3"), (sut.state.value as HomeState.Ready).players)
    }

    @Test
    fun whenPlayerRemovedWithInvalidIndex_thenDoesNothing() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.removePlayer(5)
        assertEquals(listOf("Player 1"), (sut.state.value as HomeState.Ready).players)

        sut.removePlayer(-1)
        assertEquals(listOf("Player 1"), (sut.state.value as HomeState.Ready).players)
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

    @Test
    fun whenPlayerNameUpdated_thenUpdatesPlayersList() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.addPlayer()

        sut.updatePlayerName(0, "Some player")
        sut.updatePlayerName(1, "Different")

        assertEquals(listOf("Some player", "Different"), (sut.state.value as HomeState.Ready).players)
    }

    @Test
    fun whenPlayerNameUpdatedWithInvalidIndex_thenDoesNothing() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()

        sut.updatePlayerName(5, "Out of scope")
        sut.updatePlayerName(-1, "Invalid")

        assertEquals(listOf("Player 1"), (sut.state.value as HomeState.Ready).players)
    }
}
