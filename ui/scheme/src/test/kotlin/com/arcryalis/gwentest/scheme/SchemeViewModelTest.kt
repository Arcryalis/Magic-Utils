package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.data.card.MockCardInfo
import com.arcryalis.gwentest.domain.card.MockGetShuffledCardInfoUseCase
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SchemeViewModelTest {

    companion object {
        const val DEFAULT_SET_ID = "defaultSet"
    }

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var route: SchemeRoute
    private lateinit var mockGetShuffledCardInfoUseCase: GetShuffledCardInfoUseCase

    private lateinit var sut: SchemeViewModel

    @Before
    fun setUpSut() {
        route = SchemeRoute(
            setId = DEFAULT_SET_ID
        )
        
        mockGetShuffledCardInfoUseCase = MockGetShuffledCardInfoUseCase(
            mapOf(
                DEFAULT_SET_ID to listOf(
                    MockCardInfo.cardInfo,
                    MockCardInfo.cardInfo2
                )
            )
        )

        sut = SchemeViewModel(
            route = route,
            getShuffledCardInfoUseCase = mockGetShuffledCardInfoUseCase
        )
    }

    @Test
    fun mockViewModel_whenSchemeClicked_thenSchemeAddedToFaceUpList() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)
        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialFaceUpList = initialState.faceUpCards

        assertEquals(emptyList(), initialFaceUpList)

        // add first item
        val firstClickedItem = initialCardList.first()
        sut.onSchemeClicked(firstClickedItem)

        val updatedState = sut.state.value
        val updatedCardList = (updatedState as SchemeState.Ready).cards
        val updatedFaceUpList = updatedState.faceUpCards

        assertEquals(initialCardList, updatedCardList)
        assertEquals(listOf(firstClickedItem), updatedFaceUpList)

        // add second item
        val secondClickedItem = initialCardList.last()
        sut.onSchemeClicked(secondClickedItem)

        val finalUpdatedState = sut.state.value
        val finalUpdatedCardList = (finalUpdatedState as SchemeState.Ready).cards
        val finalFaceUpList = finalUpdatedState.faceUpCards

        assertEquals(initialCardList, finalUpdatedCardList)
        assertEquals(listOf(firstClickedItem, secondClickedItem), finalFaceUpList)
    }

    @Test
    fun mockViewModel_whenSchemeClickedTwice_thenSchemeAddedToOverlay() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialFaceUpCards = initialState.faceUpCards
        val initialOverlay = initialState.overlayCard

        assertEquals(0, initialFaceUpCards.size)
        assertNull(initialOverlay)

        // first click
        val clickedItem = initialCardList.first()
        sut.onSchemeClicked(clickedItem)

        val updatedState = sut.state.value
        val updatedFaceUpCards = (updatedState as SchemeState.Ready).faceUpCards
        val updatedOverlay = updatedState.overlayCard

        assertEquals(1, updatedFaceUpCards.size)
        assertNull(updatedOverlay)

        // second click
        sut.onSchemeClicked(clickedItem)

        val finalState = sut.state.value
        val finalFaceUpCards = (finalState as SchemeState.Ready).faceUpCards
        val finalOverlay = finalState.overlayCard

        assertEquals(1, finalFaceUpCards.size)
        assertEquals(clickedItem, finalOverlay)
    }


    @Test
    fun mockViewModel_whenOngoingSchemeClicked_thenFlippedAndAddedToOngoing() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialOngoingList = initialState.ongoingCards
        val initialFaceUpList = initialState.faceUpCards
        val ongoingItem = initialCardList.firstOrNull({ it.isOngoing })

        assertNotNull(ongoingItem)
        assertEquals(true, ongoingItem.isOngoing)
        assertEquals(emptyList(), initialOngoingList)
        assertEquals(0, initialFaceUpList.size)

        // flip scheme
        sut.onSchemeClicked(ongoingItem)

        val updatedState = sut.state.value
        val updatedOngoingList = (updatedState as SchemeState.Ready).ongoingCards
        val updatedFaceUpList = updatedState.faceUpCards

        assertEquals(listOf(ongoingItem), updatedOngoingList)
        assertEquals(1, updatedFaceUpList.size)
    }

    @Test
    fun mockViewModel_whenOverlayDismissed_thenRemovedFromOverlay() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialOverlay = initialState.overlayCard
        val clickedItem = initialCardList.first()

        assertNull(initialOverlay)

        // flip scheme
        sut.onSchemeClicked(clickedItem)
        // add scheme to overlay
        sut.onSchemeClicked(clickedItem)

        val updatedState = sut.state.value
        val updatedOverlay = (updatedState as SchemeState.Ready).overlayCard

        assertEquals(clickedItem, updatedOverlay)

        // close overlay
        sut.onCloseOverlayClicked()

        val finalState = sut.state.value
        val finalOverlay = (finalState as SchemeState.Ready).overlayCard

        assertNull(finalOverlay)
    }

    @Test
    fun mockViewModel_whenAddOngoingTwice_thenAddsToOngoingOnce() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialOngoingList = initialState.ongoingCards
        val item = initialCardList.first()

        assertEquals(emptyList(), initialOngoingList)

        // add scheme
        sut.onAddOngoingClicked(item)

        val updatedState = sut.state.value
        val updatedOngoingList = (updatedState as SchemeState.Ready).ongoingCards

        assertEquals(listOf(item), updatedOngoingList)

        // second add does nothing
        sut.onAddOngoingClicked(item)

        val finalState = sut.state.value
        val finalOngoingList = (finalState as SchemeState.Ready).ongoingCards

        assertEquals(updatedOngoingList, finalOngoingList)
    }

    @Test
    fun mockViewModel_whenAddThenRemoveOngoing_thenAddsThenRemoves() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val initialOngoingList = initialState.ongoingCards
        val item = initialCardList.first()

        assertEquals(emptyList(), initialOngoingList)

        // add scheme
        sut.onAddOngoingClicked(item)

        val updatedState = sut.state.value
        val updatedOngoingList = (updatedState as SchemeState.Ready).ongoingCards

        assertEquals(listOf(item), updatedOngoingList)

        // remove scheme
        sut.onRemoveOngoingClicked(item)

        val finalState = sut.state.value
        val finalOngoingList = (finalState as SchemeState.Ready).ongoingCards

        assertEquals(emptyList(), finalOngoingList)
    }

    @Test
    fun mockViewModel_whenOnOngoingClick_thenSetsOverlay() {
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect { }}

        val initialState = sut.state.value
        assert(initialState is SchemeState.Ready)

        val initialCardList = (initialState as SchemeState.Ready).cards
        val item = initialCardList.first()

        // add scheme
        sut.onAddOngoingClicked(item)
        // click ongoing
        sut.onOngoingClicked(item)

        val updatedState = sut.state.value
        val updatedOngoingList = (updatedState as SchemeState.Ready).ongoingCards
        val updatedOverlay = updatedState.overlayCard

        assertEquals(1, updatedOngoingList.size)
        assertEquals(item, updatedOverlay)
    }
}