package com.arcryalis.gwentest.data.local.api

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity

object TestCardInfoEntity {

    val entity1 = CardInfoEntity(
        id = "cardId",
        setId = "setId",
        name = "Card Name",
        smallImageUrl = "smallUrl",
        largeImageUrl = "largeUrl",
        oracleText = "Oracle Text",
        isOngoing = true
    )

    val entity2 = CardInfoEntity(
        id = "cardId 2",
        setId = "setId the second",
        name = "A different card name",
        smallImageUrl = "The small url",
        largeImageUrl = "The large url",
        oracleText = "Some oracle text",
        isOngoing = false
    )
}