package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.domain.card.AutocompleteCardNamesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class AutocompleteCardNamesUseCaseImplTest {

    private lateinit var cardRepository: MockCardRepository
    private lateinit var sut: AutocompleteCardNamesUseCase

    private fun setupSut(
        autocompleteResult: Result<List<String>> = Result.success(emptyList())
    ) {
        cardRepository = MockCardRepository(
            availableSets = emptyList(),
            cardsBySet = emptyMap(),
            cardsExistLocally = false,
            downloadResult = false,
            autocompleteResult = autocompleteResult
        )
        sut = AutocompleteCardNamesUseCaseImpl(cardRepository)
    }

    @Test
    fun givenSuccessfulAutocomplete_whenInvoked_thenReturnsNames() = runTest {
        val names = listOf("Scheme One", "Scheme Two")
        setupSut(autocompleteResult = Result.success(names))

        val result = sut("Scheme")

        assertEquals(names, result.getOrNull())
    }

    @Test
    fun givenFailedAutocomplete_whenInvoked_thenReturnsFailure() = runTest {
        val exception = Exception("Some error")
        setupSut(autocompleteResult = Result.failure(exception))

        val result = sut("Scheme")

        assertSame(exception, result.exceptionOrNull())
    }

    @Test
    fun givenQueryWithWhitespace_whenInvoked_thenPassesTrimmedQueryToRepository() = runTest {
        setupSut()

        sut("  Scheme  ")

        assertEquals("Scheme", cardRepository.lastAutocompleteQuery)
    }
}
