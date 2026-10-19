package com.arcryalis.gwentest.home

import androidx.compose.ui.graphics.Color
import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.data.card.TestCardSet
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardSetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private class TestDownloadSchemesUseCase : DownloadSchemesUseCase {
        var invokeCount = 0
            private set

        override suspend fun invoke(): Boolean {
            invokeCount++
            return true
        }
    }

    private lateinit var testDownloadSchemesUseCase: TestDownloadSchemesUseCase
    private lateinit var sut: HomeViewModel

    private fun setupSut(
        cardSets: List<CardSet> = listOf(
            TestCardSet.set1,
            TestCardSet.set2,
        ),
    ) {
        testDownloadSchemesUseCase = TestDownloadSchemesUseCase()
        sut = HomeViewModel(
            downloadSchemesUseCase = testDownloadSchemesUseCase,
            getCardSetsUseCase = MockGetCardSetsUseCase(cardSets = cardSets),
        )
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }
    }

    private val players get() = (sut.state.value as HomeState.Ready).players

    @Test
    fun whenInitialized_thenStateIsReadyAndSchemesNotDownloaded() {
        setupSut()

        val currentState = sut.state.value
        assertIs<HomeState.Ready>(currentState)

        assertEquals(0, testDownloadSchemesUseCase.invokeCount)
        assertEquals(listOf(null, TestCardSet.set1, TestCardSet.set2), currentState.sets.items)
        assertEquals(null, currentState.sets.selectedItem)
        assertEquals(HomeItemButtonState.Available, currentState.sets.buttonState)

        assertEquals(emptyList(), currentState.players)

        assertEquals(listOf(20, 40, 60), currentState.startingLife.items)
        assertEquals(20, currentState.startingLife.selectedItem)

        assertFalse(currentState.canStartGame)
        assertNull(sut.buildGameSettings())
    }

    @Test
    fun whenPlayerAdded_thenUpdatesPlayersListWithEmptyNames() {
        setupSut()

        sut.addPlayer()
        assertEquals(listOf(""), players.map { it.name })

        sut.addPlayer()
        assertEquals(listOf("", ""), players.map { it.name })
    }

    @Test
    fun whenPlayerAddedAtMax_thenDoesNotAddMore() {
        setupSut()

        repeat(4) { sut.addPlayer() }
        assertEquals(4, players.size)

        sut.addPlayer()
        assertEquals(4, players.size)
    }

    @Test
    fun whenPlayerRemoved_thenUpdatesPlayersList() {
        setupSut()

        sut.addPlayer()
        sut.addPlayer()
        sut.addPlayer()

        sut.removePlayer(1)
        assertEquals(listOf("", ""), players.map { it.name })
    }

    @Test
    fun whenPlayerRemovedWithInvalidIndex_thenDoesNothing() {
        setupSut()

        sut.addPlayer()
        sut.removePlayer(5)
        assertEquals(listOf(""), players.map { it.name })

        sut.removePlayer(-1)
        assertEquals(listOf(""), players.map { it.name })
    }

    @Test
    fun whenLifeTotalSelected_thenUpdatesLifeTotalState() {
        setupSut()

        sut.onSelectLifeTotal(40)

        val updatedState = sut.state.value
        assertIs<HomeState.Ready>(updatedState)
        assertEquals(40, updatedState.startingLife.selectedItem)
    }

    @Test
    fun whenPlayerNameUpdated_thenUpdatesPlayersList() {
        setupSut()

        sut.addPlayer()
        sut.addPlayer()

        sut.updatePlayerName(0, "Some player")
        sut.updatePlayerName(1, "Different")

        assertEquals(listOf("Some player", "Different"), players.map { it.name })
    }

    @Test
    fun whenPlayerNameUpdatedWithInvalidIndex_thenDoesNothing() {
        setupSut()

        sut.addPlayer()

        sut.updatePlayerName(5, "Out of scope")
        sut.updatePlayerName(-1, "Invalid")

        assertEquals(listOf(""), players.map { it.name })
    }

    @Test
    fun givenPlayersLifeTotalAndSet_whenBuildGameSettings_thenReturnsSelectedSettings() {
        setupSut()

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
    fun givenSetOnly_whenBuildGameSettings_thenCanStartGameWithNoPlayers() {
        setupSut()

        sut.onSelectSet(TestCardSet.set1)

        assertTrue((sut.state.value as HomeState.Ready).canStartGame)
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
    fun givenPlayersOnly_whenBuildGameSettings_thenCanStartGameWithNoSet() {
        setupSut()

        sut.addPlayer()

        assertTrue((sut.state.value as HomeState.Ready).canStartGame)
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
