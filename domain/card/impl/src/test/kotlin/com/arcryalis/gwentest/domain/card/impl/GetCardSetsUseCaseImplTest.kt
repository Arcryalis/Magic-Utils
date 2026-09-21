package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.data.card.MockCardSet
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.card.GetCardSetsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetCardSetsUseCaseImplTest {

    private lateinit var sut: GetCardSetsUseCase

    private fun setupSut(
        availableSets: List<CardSet>
    ) {
        sut = GetCardSetsUseCaseImpl(
            MockCardRepository(
                availableSets = availableSets,
                cardsBySet = emptyMap(),
                cardsExistLocally = false,
                downloadResult = false
            )
        )
    }

    @Test
    fun givenAvailableSets_whenInvoked_thenReturnsSets() = runTest {
        val setList = listOf(MockCardSet.set1, MockCardSet.set2)
        setupSut(availableSets = setList)

        val result = sut().first()

        assertEquals(setList, result)
    }

    @Test
    fun givenNoSets_whenInvoked_thenReturnsEmptyList() = runTest {
        setupSut(availableSets = emptyList())

        val result = sut().first()

        assertEquals(emptyList(), result)
    }
}