package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import org.junit.Test
import kotlin.test.assertEquals

class CardInfoToModelMapperTest {

    private val cardBackUrl = "cardBackUrl"

    @Test
    fun givenCardInfoEntity_whenToModel_thenReturnsCardInfo() {
        val entity = TestScryfallDataDto.cardInfoEntity
        val expectedModel = TestCardInfo.info1

        val result = entity.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithNullOracleText_whenToModel_thenReturnsCardInfoWithNullOracleText() {
        val entity = TestScryfallDataDto.cardInfoEntity.copy(oracleText = null)
        val expectedModel = TestCardInfo.info1.copy(
            oracleText = null,
            images = TestCardInfo.info1.images.copy(
                back = cardBackUrl
            )
        )

        val result = entity.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithOngoingFalse_whenToModel_thenReturnsCardInfoOngoingFalse() {
        val entity = TestScryfallDataDto.cardInfoEntity.copy(isOngoing = false)
        val expectedModel = CardInfo(
            name = entity.name,
            oracleText = entity.oracleText,
            images = CardInfoImageUrls(
                small = entity.smallImageUrl,
                large = entity.largeImageUrl,
                back = cardBackUrl
            ),
            isOngoing = false
        )

        val result = entity.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntities_whenToModelList_thenReturnsCardInfos() {
        val entity1 = TestScryfallDataDto.cardInfoEntity
        val entity2 = TestScryfallDataDto.cardInfoEntity2

        val expectedModel = listOf(
            CardInfo(
                name = entity1.name,
                oracleText = entity1.oracleText,
                images = CardInfoImageUrls(
                    small = entity1.smallImageUrl,
                    large = entity1.largeImageUrl,
                    back = cardBackUrl
                ),
                isOngoing = entity1.isOngoing
            ),
            CardInfo(
                name = entity2.name,
                oracleText = entity2.oracleText,
                images = CardInfoImageUrls(
                    small = entity2.smallImageUrl,
                    large = entity2.largeImageUrl,
                    back = cardBackUrl
                ),
                isOngoing = entity2.isOngoing
            )
        )
        val entities = listOf(entity1, entity2)

        val result = entities.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }
}