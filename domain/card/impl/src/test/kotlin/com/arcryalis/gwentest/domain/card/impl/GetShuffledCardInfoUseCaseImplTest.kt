package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetShuffledCardInfoUseCaseImplTest {

    private lateinit var sut: GetShuffledCardInfoUseCase

    private fun setupSut(
        cardsBySet: Map<String, List<CardInfo>>
    ) {
        sut = GetShuffledCardInfoUseCaseImpl(
            MockCardRepository(
                availableSets = emptyList(),
                cardsBySet = cardsBySet,
                cardsExistLocally = false,
                downloadResult = false
            )
        )
    }

    @Test
    fun givenCardsForSet_whenInvoked_thenReturnsCards() = runTest {
        val setId = "setId"
        val cards = listOf(TestCardInfo.info1, TestCardInfo.info2)

        setupSut(
            cardsBySet = mapOf(
                setId to cards,
                "differentId" to listOf(TestCardInfo.info1),
            )
        )

        val result = sut(setId).first()

        assertEquals(cards.size, result.size)
    }

    @Test
    fun givenUnknownSet_whenInvoked_thenReturnsEmptyList() = runTest {
        val setId = "set1"
        val incorrectSetId = "set2"
        val cards = listOf(TestCardInfo.info1)
        setupSut(
            cardsBySet = mapOf(setId to cards)
        )

        val result = sut(incorrectSetId).first()

        assertEquals(emptyList(), result)
    }
}