package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadSchemesUseCaseImplTest {

    private lateinit var sut: DownloadSchemesUseCase

    private fun setupSut(
        downloadResult: Boolean
    ) {
        sut = DownloadSchemesUseCaseImpl(
            MockCardRepository(
                availableSets = emptyList(),
                cardsBySet = emptyMap(),
                cardsExistLocally = false,
                downloadResult = downloadResult
            )
        )
    }

    @Test
    fun givenSuccessfulDownload_whenInvoked_thenReturnsTrue() = runTest {
        setupSut(downloadResult = true)

        val result = sut()

        assertTrue(result)
    }

    @Test
    fun givenFailedDownload_whenInvoked_thenReturnsFalse() = runTest {
        setupSut(downloadResult = false)

        val result = sut()

        assertFalse(result)
    }
}