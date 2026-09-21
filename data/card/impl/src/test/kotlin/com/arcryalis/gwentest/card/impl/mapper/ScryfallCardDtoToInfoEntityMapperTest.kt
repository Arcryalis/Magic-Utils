package com.arcryalis.gwentest.card.impl.mapper

import org.junit.Test
import kotlin.test.assertEquals

class ScryfallCardDtoToInfoEntityMapperTest {

    @Test
    fun givenScryfallCardDto_whenToInfoEntity_thenReturnsCardInfoEntity() {
        val dto = TestScryfallDataDto.scryfallCardDto
        val expectedEntity = TestScryfallDataDto.cardInfoEntity

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithOngoingType_whenToInfoEntity_thenIsOngoingTrue() {
        val dto = TestScryfallDataDto.scryfallCardDto.copy(type = "Scheme - Ongoing")
        val expectedEntity = TestScryfallDataDto.cardInfoEntity.copy(isOngoing = true)

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutOngoingType_whenToInfoEntity_thenIsOngoingFalse() {
        val dto = TestScryfallDataDto.scryfallCardDto.copy(type = "Instant")
        val expectedEntity = TestScryfallDataDto.cardInfoEntity.copy(isOngoing = false)

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutImageUris_whenToInfoEntity_thenEmptyImageUrls() {
        val dto = TestScryfallDataDto.scryfallCardDto.copy(imageUris = null)
        val expectedEntity = TestScryfallDataDto.cardInfoEntity.copy(
            smallImageUrl = "",
            largeImageUrl = ""
        )

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtos_whenToInfoEntityList_thenReturnsCardInfoEntities() {
        val dtos = listOf(
            TestScryfallDataDto.scryfallCardDto,
            TestScryfallDataDto.scryfallCardDto.copy(
                id = "cardId2",
                name = "Card Name 2",
                type = "Instant"
            )
        )
        val expectedEntities = listOf(
            TestScryfallDataDto.cardInfoEntity,
            TestScryfallDataDto.cardInfoEntity.copy(
                id = "cardId2",
                name = "Card Name 2",
                isOngoing = false
            )
        )

        val result = dtos.toInfoEntity()

        assertEquals(expectedEntities, result)
    }
}