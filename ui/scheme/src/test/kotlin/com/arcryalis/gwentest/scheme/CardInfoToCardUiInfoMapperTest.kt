package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.scheme.mapper.toUiModel
import org.junit.Test
import kotlin.test.assertEquals

class CardInfoToCardUiInfoMapperTest {

    @Test
    fun givenCardInfo_whenToUiModel_thenReturnsCardUiInfo() {
        val cardInfo = CardInfo(
            name = "Card Name",
            oracleText = "Oracle Text",
            images = CardInfoImageUrls(
                small = "smallUrl",
                large = "largeUrl",
                back = "backUrl"
            ),
            isOngoing = true
        )

        val expected = TestCardUiInfo.cardUiInfo

        val result = cardInfo.toUiModel()

        assertEquals(expected, result)
    }

    @Test
    fun givenCardInfoWithOngoingFalse_whenToUiModel_thenReturnsCardUiInfo() {
        val cardInfo = CardInfo(
            name = "Card Name",
            oracleText = "Oracle Text",
            images = CardInfoImageUrls(
                small = "smallUrl",
                large = "largeUrl",
                back = "backUrl"
            ),
            isOngoing = false
        )

        val expected = TestCardUiInfo.cardUiInfo.copy(
            isOngoing = false
        )

        val result = cardInfo.toUiModel()

        assertEquals(expected, result)
    }

}