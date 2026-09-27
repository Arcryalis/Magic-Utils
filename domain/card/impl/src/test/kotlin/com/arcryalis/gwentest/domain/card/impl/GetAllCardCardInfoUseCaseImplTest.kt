package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardCardInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllCardCardInfoUseCaseImplTest {

    private lateinit var sut: GetAllCardCardInfoUseCase

    private fun setupSut(
        cardsBySet: Map<String, List<CardInfo>>
    ) {
        sut = GetAllCardCardInfoUseCaseImpl(
            MockCardRepository(
                availableSets = emptyList(),
                cardsBySet = cardsBySet,
                cardsExistLocally = false,
                downloadResult = false
            )
        )
    }

    @Test
    fun givenStoredCards_whenInvoked_thenReturnsAllCards() = runTest {
        val setId1 = "set1"
        val setId2 = "set2"
        val cards1 = listOf(TestCardInfo.info1)
        val cards2 = listOf(TestCardInfo.info2)

        setupSut(
            cardsBySet = mapOf(
                setId1 to cards1,
                setId2 to cards2
            )
        )

        val result = sut().first()

        assertEquals(listOf(TestCardInfo.info1, TestCardInfo.info2), result)
    }

    @Test
    fun givenNoStoredCards_whenInvoked_thenReturnsEmptyList() = runTest {
        setupSut(
            cardsBySet = emptyMap()
        )

        val result = sut().first()

        assertEquals(emptyList(), result)
    }
}
