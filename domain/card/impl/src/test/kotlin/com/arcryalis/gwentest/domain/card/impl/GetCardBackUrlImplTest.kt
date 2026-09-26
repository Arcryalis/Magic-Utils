package com.arcryalis.gwentest.domain.card.impl

import com.arcryalis.gwentest.data.card.MockCardRepository
import com.arcryalis.gwentest.domain.card.GetCardBackUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GetCardBackUrlImplTest {

    private lateinit var sut: GetCardBackUrl

    @Test
    fun whenInvoked_thenReturnsCardBackUrl() = runTest {
        val expectedUrl = "https://example.com/back.png"
        sut = GetCardBackUrlImpl(
            MockCardRepository(
                availableSets = emptyList(),
                cardsBySet = emptyMap(),
                cardsExistLocally = false,
                downloadResult = false,
                cardBackUrl = expectedUrl
            )
        )

        val result = sut()

        assertEquals(expectedUrl, result)
    }
}
