package com.arcryalis.gwentest.card.impl.mapper

import org.junit.Test
import kotlin.test.assertEquals

class ScryfallCardDtoToInfoEntityMapperTest {

    @Test
    fun givenScryfallCardDto_whenToInfoEntity_thenReturnsCardInfoEntity() {
        val dto = TestCardData.scryfallCardDto
        val expectedEntity = TestCardData.cardInfoEntity

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithOngoingType_whenToInfoEntity_thenIsOngoingTrue() {
        val dto = TestCardData.scryfallCardDto.copy(type = "Scheme - Ongoing")
        val expectedEntity = TestCardData.cardInfoEntity.copy(isOngoing = true)

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutOngoingType_whenToInfoEntity_thenIsOngoingFalse() {
        val dto = TestCardData.scryfallCardDto.copy(type = "Instant")
        val expectedEntity = TestCardData.cardInfoEntity.copy(isOngoing = false)

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutImageUris_whenToInfoEntity_thenEmptyImageUrls() {
        val dto = TestCardData.scryfallCardDto.copy(imageUris = null)
        val expectedEntity = TestCardData.cardInfoEntity.copy(
            smallImageUrl = "",
            largeImageUrl = ""
        )

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtos_whenToInfoEntityList_thenReturnsCardInfoEntities() {
        val dtos = listOf(
            TestCardData.scryfallCardDto,
            TestCardData.scryfallCardDto.copy(
                id = "cardId2",
                name = "Card Name 2",
                type = "Instant"
            )
        )
        val expectedEntities = listOf(
            TestCardData.cardInfoEntity,
            TestCardData.cardInfoEntity.copy(
                id = "cardId2",
                name = "Card Name 2",
                isOngoing = false
            )
        )

        val result = dtos.toInfoEntity()

        assertEquals(expectedEntities, result)
    }
}