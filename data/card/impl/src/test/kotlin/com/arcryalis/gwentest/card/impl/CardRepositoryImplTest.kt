package com.arcryalis.gwentest.card.impl

import android.content.Context
import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.MockScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallPaginationDto
import com.arcryalis.gwentest.card.impl.mapper.TestScryfallDataDto
import com.arcryalis.gwentest.card.impl.mapper.TestCardInfoEntity
import com.arcryalis.gwentest.card.impl.mapper.TestCardSetEntity
import com.arcryalis.gwentest.data.card.impl.R
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.MockCardInfoDataStore
import com.arcryalis.gwentest.data.local.api.MockCardSetDataStore
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import com.arcryalis.gwentest.network.MockCoilImageCacher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CardRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var storedCards: MutableMap<String, MutableList<CardInfoEntity>>
    private lateinit var storedSets: MutableList<CardSetEntity>
    private lateinit var responses: MutableMap<Int, RemoteResponse<ScryfallPaginationDto>>
    private lateinit var cardInfoStore: MockCardInfoDataStore
    private lateinit var cardSetStore: MockCardSetDataStore
    private lateinit var dataSource: MockScryfallDataSource
    private lateinit var imageCacher: MockCoilImageCacher

    private lateinit var sut: CardRepositoryImpl

    @Before
    fun setupSut() {
        context = RuntimeEnvironment.getApplication()

        storedCards = mutableMapOf()
        storedSets = mutableListOf()
        responses = mutableMapOf()
        cardInfoStore = MockCardInfoDataStore(storedCards)
        cardSetStore = MockCardSetDataStore(storedSets)
        dataSource = MockScryfallDataSource(responses)
        imageCacher = MockCoilImageCacher()

        sut = CardRepositoryImpl(
            cardInfoStore = cardInfoStore,
            cardSetStore = cardSetStore,
            dataSource = dataSource,
            imageCacher = imageCacher,
            context = context
        )
    }

    @Test
    fun givenNoLocalCards_whenDoCardsExistLocally_thenReturnsFalse() = runTest {
        val result = sut.doCardsExistLocally()

        assertFalse(result)
    }

    @Test
    fun givenLocalCards_whenDoCardsExistLocally_thenReturnsTrue() = runTest {
        val entity = TestCardInfoEntity.entity1
        val setId = entity.setId
        storedCards[setId] = mutableListOf(entity)

        val result = sut.doCardsExistLocally()

        assertTrue(result)
    }

    @Test
    fun givenSuccessfulResponse_whenDownloadSchemes_thenReturnsTrue() = runTest {
        responses[1] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = false,
                data = listOf(TestScryfallDataDto.scryfallCardDto)
            )
        )

        val result = sut.downloadSchemes()

        assertTrue(result)
    }

    @Test
    fun givenSuccessfulResponse_whenDownloadSchemes_thenPersistsSetsAndCards() = runTest {
        val dto = TestScryfallDataDto.scryfallCardDto.copy(
            type = "Type that includes ongoing"
        )
        val setEntity = CardSetEntity(
            id = dto.setId,
            name = dto.setName
        )
        val cardEntity = CardInfoEntity(
            id = dto.name,
            setId = dto.setId,
            name = dto.name,
            smallImageUrl = dto.imageUris!!.small,
            largeImageUrl = dto.imageUris!!.large,
            oracleText = dto.oracleText,
            isOngoing = true
        )

        responses[1] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = false,
                data = listOf(dto)
            )
        )

        sut.downloadSchemes()

        assertEquals(listOf(setEntity), storedSets)
        assertContentEquals(
            listOf(cardEntity),
            storedCards.values.flatten()
        )
    }

    @Test
    fun givenSuccessfulResponse_whenDownloadSchemes_thenCachesCardBackAndFaceImages() = runTest {
        val entity = TestScryfallDataDto.scryfallCardDto
        val smallUrl = entity.imageUris!!.small
        val largeUrl = entity.imageUris!!.large
        val cardBackUrl = context.getString(R.string.card_back_url)

        responses[1] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = false,
                data = listOf(entity)
            )
        )

        sut.downloadSchemes()

        assertEquals(
            listOf(cardBackUrl, smallUrl, largeUrl),
            imageCacher.requestedUrls
        )
    }

    @Test
    fun givenCardsWithDuplicateImageUrls_whenDownloadSchemes_thenCachesDistinctUrls() = runTest {
        val entity = TestScryfallDataDto.scryfallCardDto
        val smallUrl = entity.imageUris!!.small
        val largeUrl = entity.imageUris!!.large
        val cardBackUrl = context.getString(R.string.card_back_url)

        val entity2 = TestScryfallDataDto.scryfallCardDto2.copy(
            imageUris = TestScryfallDataDto.scryfallCardDto2.imageUris!!.copy(
                // keep small different
                large = largeUrl
            )
        )
        val smallUrl2 = entity2.imageUris!!.small

        responses[1] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = false,
                data = listOf(TestScryfallDataDto.scryfallCardDto, entity2)
            )
        )

        sut.downloadSchemes()

        assertContentEquals(
            listOf(cardBackUrl, smallUrl, smallUrl2, largeUrl),
            imageCacher.requestedUrls
        )
    }

    @Test
    fun givenMoreResultsOnFirstPage_whenDownloadSchemes_thenFetchesSubsequentPages() = runTest {
        responses[1] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = true,
                data = listOf(TestScryfallDataDto.scryfallCardDto)
            )
        )
        responses[2] = RemoteResponse.Success(
            ScryfallPaginationDto(
                hasMore = false,
                data = listOf(TestScryfallDataDto.scryfallCardDto2)
            )
        )

        val result = sut.downloadSchemes()

        assertTrue(result)
        assertEquals(2, storedSets.size)
    }

    @Test
    fun givenErrorResponse_whenDownloadSchemes_thenReturnsFalse() = runTest {
        responses[1] = RemoteResponse.Error("Some error")

        val result = sut.downloadSchemes()

        assertFalse(result)
    }

    @Test
    fun givenErrorResponse_whenDownloadSchemes_thenDoesNotPersistData() = runTest {
        responses[1] = RemoteResponse.Error("Some error")

        sut.downloadSchemes()

        assertEquals(emptyList(), storedSets)
    }

    @Test
    fun givenNoConfiguredResponse_whenDownloadSchemes_thenReturnsFalse() = runTest {
        val result = sut.downloadSchemes()

        assertFalse(result)
    }

    @Test
    fun givenStoredSets_whenGetAvailableSets_thenReturnsMappedSets() = runTest {
        val entity = TestCardSetEntity.set1
        val expectedSet = CardSet(
            id = entity.id,
            name = entity.name
        )

        storedSets.add(TestCardSetEntity.set1)

        val result = sut.getAvailableSets().first()

        assertEquals(listOf(expectedSet), result)
    }

    @Test
    fun givenNoStoredSets_whenGetAvailableSets_thenReturnsEmptyList() = runTest {
        val result = sut.getAvailableSets().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun givenStoredCardsForSet_whenGetSet_thenReturnsCardsWithCardBackUrl() = runTest {
        val entity = TestCardInfoEntity.entity1
        val setId = entity.setId
        storedCards[setId] = mutableListOf(entity)

        val expected = CardInfo(
            name = entity.name,
            oracleText = entity.oracleText,
            images = CardInfoImageUrls(
                small = entity.smallImageUrl,
                large = entity.largeImageUrl,
                back = context.getString(R.string.card_back_url)
            ),
            isOngoing = entity.isOngoing
        )

        val result = sut.getSet(setId).first()

        assertEquals(listOf(expected), result)
    }

    @Test
    fun givenNoStoredCardsForSet_whenGetSet_thenReturnsEmptyList() = runTest {
        val entity = TestCardInfoEntity.entity1
        storedCards[entity.setId] = mutableListOf(entity)

        val result = sut.getSet("unknownSetId").first()

        assertEquals(emptyList(), result)
    }
}