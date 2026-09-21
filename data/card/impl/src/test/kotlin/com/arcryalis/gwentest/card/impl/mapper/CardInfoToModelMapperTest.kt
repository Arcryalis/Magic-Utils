package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.data.local.api.TestCardInfoEntity
import org.junit.Test
import kotlin.test.assertEquals

class CardInfoToModelMapperTest {

    private val cardBackUrl = "cardBackUrl"

    @Test
    fun givenCardInfoEntity_whenToModel_thenReturnsCardInfo() {
        val entity = TestCardInfoEntity.entity1
        val expectedModel = CardInfo(
            name = entity.name,
            oracleText = entity.oracleText,
            images = CardInfoImageUrls(
                small = entity.smallImageUrl,
                large = entity.largeImageUrl,
                back = cardBackUrl
            ),
            isOngoing = entity.isOngoing
        )

        val result = entity.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithNullOracleText_whenToModel_thenReturnsCardInfoWithNullOracleText() {
        val entity = TestCardInfoEntity.entity1.copy(oracleText = null)
        val expectedModel = CardInfo(
            name = entity.name,
            oracleText = entity.oracleText,
            images = CardInfoImageUrls(
                small = entity.smallImageUrl,
                large = entity.largeImageUrl,
                back = cardBackUrl
            ),
            isOngoing = entity.isOngoing
        )

        val result = entity.toModel(cardBackUrl)

        assertEquals(expectedModel, result)
    }

    @Test
    fun givenCardInfoEntityWithOngoingFalse_whenToModel_thenReturnsCardInfoOngoingFalse() {
        val entity = TestCardInfoEntity.entity1.copy(isOngoing = false)
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
        val entity1 = TestCardInfoEntity.entity1
        val entity2 = TestCardInfoEntity.entity2

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