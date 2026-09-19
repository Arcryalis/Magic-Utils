package com.arcryalis.gwentest.card.impl.mapper

import org.junit.Test
import kotlin.test.assertEquals

class CardInfoToModelMapperTest {

    @Test
    fun givenCardInfoEntity_whenToModel_thenReturnsCardInfo() {
        val entity = TestCardData.cardInfoEntity
        val cardBack = TestCardData.cardBackUrl
        val expectedModel = TestCardData.cardInfo

        val result = entity.toModel(cardBack)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithNullOracleText_whenToModel_thenReturnsCardInfoWithNullOracleText() {
        val entity = TestCardData.cardInfoEntity.copy(oracleText = null)
        val cardBack = TestCardData.cardBackUrl
        val expectedModel = TestCardData.cardInfo.copy(oracleText = null)

        val result = entity.toModel(cardBack)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithOngoingFalse_whenToModel_thenReturnsCardInfoOngoingFalse() {
        val entity = TestCardData.cardInfoEntity.copy(isOngoing = false)
        val cardBack = TestCardData.cardBackUrl
        val expectedModel = TestCardData.cardInfo.copy(isOngoing = false)

        val result = entity.toModel(cardBack)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntities_whenToModelList_thenReturnsCardInfos() {
        val entities = listOf(
            TestCardData.cardInfoEntity,
            TestCardData.cardInfoEntity.copy(
                id = "cardId2",
                name = "Card Name 2",
                isOngoing = false
            )
        )
        val cardBack = TestCardData.cardBackUrl
        val expectedModel = listOf(
            TestCardData.cardInfo,
            TestCardData.cardInfo.copy(
                name = "Card Name 2",
                isOngoing = false
            )
        )

        val result = entities.toModel(cardBack)

        assertEquals(expectedModel, result)
    }
}