package com.arcryalis.gwentest.card.impl.mapper

import org.junit.Test
import kotlin.test.assertEquals

class CardSetEntityToModelMapperTest {

    @Test
    fun givenCardSetEntity_whenToModel_thenReturnsCardSet() {
        val entity = TestCardData.cardSetEntity
        val expectedModel = TestCardData.cardSet

        val result = entity.toModel()

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardSetEntities_whenToModelList_thenReturnsCardSets() {
        val entities = listOf(
            TestCardData.cardSetEntity,
            TestCardData.cardSetEntity.copy(
                id = "setId2",
                name = "Set Name 2"
            )
        )
        val expectedModel = listOf(
            TestCardData.cardSet,
            TestCardData.cardSet.copy(
                id = "setId2",
                name = "Set Name 2"
            )
        )

        val result = entities.toModel()

        assertEquals(expectedModel, result)
    }
}