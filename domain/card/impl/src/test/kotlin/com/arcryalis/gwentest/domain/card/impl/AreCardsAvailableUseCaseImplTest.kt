package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AreCardsAvailableUseCaseImplTest {

    private lateinit var sut: AreCardsAvailableUseCase

    private fun setupSut(
        cardsExist: Boolean
    ) {
        sut = AreCardsAvailableUseCaseImpl(
            MockCardRepository(
                availableSets = emptyList(),
                cardsBySet = emptyMap(),
                cardsExistLocally = cardsExist,
                downloadResult = false
            )
        )
    }

    @Test
    fun givenCardsExistLocally_whenInvoked_thenReturnsTrue() = runTest {
        setupSut(cardsExist = true)

        val result = sut().first()

        assertTrue(result)
    }

    @Test
    fun givenNoCardsExist_whenInvoked_thenReturnsFalse() = runTest {
        setupSut(cardsExist = false)

        val result = sut().first()

        assertFalse(result)
    }
}
