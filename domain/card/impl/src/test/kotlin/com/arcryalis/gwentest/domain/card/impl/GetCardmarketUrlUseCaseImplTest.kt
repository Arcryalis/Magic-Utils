package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.domain.card.GetCardmarketUrlUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class GetCardmarketUrlUseCaseImplTest {

    private lateinit var cardRepository: MockCardRepository
    private lateinit var sut: GetCardmarketUrlUseCase

    private fun setupSut(
        cardmarketUrlResult: Result<String> = Result.success("https://www.cardmarket.com/en/Magic/Products?idProduct=1")
    ) {
        cardRepository = MockCardRepository(
            availableSets = emptyList(),
            cardsBySet = emptyMap(),
            cardsExistLocally = false,
            downloadResult = false,
            cardmarketUrlResult = cardmarketUrlResult
        )
        sut = GetCardmarketUrlUseCaseImpl(cardRepository)
    }

    @Test
    fun givenCardmarketUrl_whenInvoked_thenReturnsUrl() = runTest {
        val url = "https://www.cardmarket.com/en/Magic/Products?idProduct=891653"
        setupSut(cardmarketUrlResult = Result.success(url))

        val result = sut("Scheme One")

        assertEquals(url, result.getOrNull())
    }

    @Test
    fun givenFailedLookup_whenInvoked_thenReturnsFailure() = runTest {
        val exception = Exception("Some error")
        setupSut(cardmarketUrlResult = Result.failure(exception))

        val result = sut("Scheme One")

        assertSame(exception, result.exceptionOrNull())
    }

    @Test
    fun givenCardName_whenInvoked_thenPassesNameToRepository() = runTest {
        setupSut()

        sut("Scheme One")

        assertEquals("Scheme One", cardRepository.lastCardmarketQuery)
    }
}
