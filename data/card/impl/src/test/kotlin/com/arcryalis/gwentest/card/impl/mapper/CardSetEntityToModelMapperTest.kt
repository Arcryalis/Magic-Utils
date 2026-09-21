package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.TestCardSetEntity
import org.junit.Test
import kotlin.test.assertEquals

class CardSetEntityToModelMapperTest {

    @Test
    fun givenCardSetEntity_whenToModel_thenReturnsCardSet() {
        val entity = TestCardSetEntity.set1
        val expectedModel = CardSet(
            id = entity.id,
            name = entity.name
        )

        val result = entity.toModel()

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardSetEntities_whenToModelList_thenReturnsCardSets() {
        val firstEntity = TestCardSetEntity.set1
        val secondEntity = TestCardSetEntity.set2

        val entities = listOf(firstEntity,secondEntity)
        val expectedModel = listOf(
            CardSet(
                id = firstEntity.id,
                name = firstEntity.name
            ),
            CardSet(
                id = secondEntity.id,
                name = secondEntity.name
            )
        )

        val result = entities.toModel()

        assertEquals(expectedModel, result)
    }
}