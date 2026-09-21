package com.arcryalis.gwentest.card.impl.mapper

import org.junit.Test
import kotlin.test.assertEquals

class ScryfallCardDtoToSetEntityMapperTest {

    @Test
    fun givenScryfallCardDto_whenToSetEntity_thenReturnsCardSetEntity() {
        val dto = TestScryfallDataDto.scryfallCardDto
        val expectedEntity = TestScryfallDataDto.cardSetEntity

        val result = dto.toSetEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtos_whenToDistinctSetEntity_thenReturnsSetEntities() {
        val dtos = listOf(
            TestScryfallDataDto.scryfallCardDto,
            TestScryfallDataDto.scryfallCardDto.copy(
                id = "cardId2",
                name = "Card Name 2",
                setId = "setId2",
                setName = "Set Name 2"
            )
        )
        val expectedEntities = listOf(
            TestScryfallDataDto.cardSetEntity,
            TestScryfallDataDto.cardSetEntity.copy(
                id = "setId2",
                name = "Set Name 2"
            )
        )

        val result = dtos.toDistinctSetEntity()

        assertEquals(expectedEntities, result)
    }

    @Test
    fun givenScryfallCardDtosWithDuplicateSetIds_whenToDistinctSetEntity_thenRemovesDuplicates() {
        val dtos = listOf(
            TestScryfallDataDto.scryfallCardDto,
            TestScryfallDataDto.scryfallCardDto.copy(
                id = "cardId2",
                name = "Card Name 2"
            ),
            TestScryfallDataDto.scryfallCardDto.copy(
                id = "cardId3",
                name = "Card Name 3"
            )
        )
        val expectedEntities = listOf(TestScryfallDataDto.cardSetEntity)

        val result = dtos.toDistinctSetEntity()

        assertEquals(expectedEntities, result)
    }
}