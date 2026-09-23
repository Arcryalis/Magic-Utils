package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import com.arcryalis.gwentest.remote.scryfall.TestScryfallDataDto
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ScryfallCardDtoToSetEntityMapperTest {

    @Test
    fun givenScryfallCardDto_whenToSetEntity_thenReturnsCardSetEntity() {
        val dto = TestScryfallDataDto.dto1
        val expectedEntity = CardSetEntity(
            id = dto.setId,
            name = dto.setName
        )

        val result = dto.toSetEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtos_whenToDistinctSetEntity_thenReturnsSetEntities() {
        val dto = TestScryfallDataDto.dto1
        val dto2 = TestScryfallDataDto.dto2
        val expectedEntities = listOf(
            CardSetEntity(
                id = dto.setId,
                name = dto.setName
            ),
            CardSetEntity(
                id = dto2.setId,
                name = dto2.setName
            ),
        )

        val result = listOf(dto, dto2).toDistinctSetEntity()

        assertEquals(expectedEntities, result)
    }

    @Test
    fun givenScryfallCardDtosWithDuplicateSetIds_whenToDistinctSetEntity_thenRemovesDuplicates() {
        val entity = TestScryfallDataDto.dto1
        val dtos = listOf(
            entity,
            entity.copy(
                id = "cardId2",
                name = "Card Name 2"
            ),
            entity.copy(
                id = "cardId3",
                name = "Card Name 3"
            )
        )
        val expectedEntities = listOf(
            CardSetEntity(
                id = entity.setId,
                name = entity.setName
            )
        )

        val result = dtos.toDistinctSetEntity()

        assertContentEquals(expectedEntities, result)
    }
}