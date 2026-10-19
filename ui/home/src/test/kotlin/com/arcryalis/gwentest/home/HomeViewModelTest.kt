package com.arcryalis.gwentest.home

import androidx.compose.ui.graphics.Color
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
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
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
    fun whenPlayerAdded_thenUpdatesPlayersListWithEmptyNames() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        val initialState = sut.state.value
        assertIs<HomeState.Ready>(initialState)
        assertEquals(emptyList<HomePlayerInfo>(), initialState.players)

        sut.addPlayer()
        val result = (sut.state.value as? HomeState.Ready)?.players?.map { it.name }

        assertEquals(listOf(""), result)

        sut.addPlayer()
        val updatedResult = (sut.state.value as? HomeState.Ready)?.players?.map { it.name }
        assertEquals(listOf("", ""), updatedResult)
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
        assertEquals(listOf("", ""), (sut.state.value as HomeState.Ready).players.map { it.name })
    }

    @Test
    fun whenPlayerRemovedWithInvalidIndex_thenDoesNothing() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.removePlayer(5)
        assertEquals(listOf(""), (sut.state.value as HomeState.Ready).players.map { it.name })

        sut.removePlayer(-1)
        assertEquals(listOf(""), (sut.state.value as HomeState.Ready).players.map { it.name })
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

        assertEquals(listOf("Some player", "Different"), (sut.state.value as HomeState.Ready).players.map { it.name })
    }

    @Test
    fun whenPlayerNameUpdatedWithInvalidIndex_thenDoesNothing() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()

        sut.updatePlayerName(5, "Out of scope")
        sut.updatePlayerName(-1, "Invalid")

        assertEquals(listOf(""), (sut.state.value as HomeState.Ready).players.map { it.name })
    }

    @Test
    fun givenNoPlayersAndNoSet_whenInitialized_thenCannotStartGame() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        assertFalse((sut.state.value as HomeState.Ready).canStartGame)
        assertNull(sut.buildGameSettings())
    }

    @Test
    fun whenPlayerAdded_thenCanStartGame() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()

        assertTrue((sut.state.value as HomeState.Ready).canStartGame)
    }

    @Test
    fun whenSetSelected_thenCanStartGame() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onSelectSet(TestCardSet.set1)

        assertTrue((sut.state.value as HomeState.Ready).canStartGame)
    }

    @Test
    fun whenLastPlayerRemovedWithNoSet_thenCannotStartGame() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.removePlayer(0)

        assertFalse((sut.state.value as HomeState.Ready).canStartGame)
        assertNull(sut.buildGameSettings())
    }

    @Test
    fun givenPlayersLifeTotalAndSet_whenBuildGameSettings_thenReturnsSelectedSettings() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()
        sut.addPlayer()
        sut.updatePlayerName(0, "Player 1")
        sut.updatePlayerColor(0, Color.Red)
        sut.updatePlayerName(1, "Player 2")
        sut.onSelectLifeTotal(40)
        sut.onSelectSet(TestCardSet.set2)

        assertEquals(
            HomeGameSettings(
                players = listOf(
                    HomePlayerInfo("Player 1", Color.Red),
                    HomePlayerInfo("Player 2"),
                ),
                startingLife = 40,
                setId = TestCardSet.set2.id
            ),
            sut.buildGameSettings()
        )
    }

    @Test
    fun givenSetOnly_whenBuildGameSettings_thenReturnsNoPlayers() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.onSelectSet(TestCardSet.set1)

        assertEquals(
            HomeGameSettings(
                players = emptyList(),
                startingLife = 20,
                setId = TestCardSet.set1.id
            ),
            sut.buildGameSettings()
        )
    }

    @Test
    fun givenPlayersOnly_whenBuildGameSettings_thenReturnsNoSet() {
        sut = createSut()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }

        sut.addPlayer()

        assertEquals(
            HomeGameSettings(
                players = listOf(HomePlayerInfo()),
                startingLife = 20,
                setId = null
            ),
            sut.buildGameSettings()
        )
    }
}
