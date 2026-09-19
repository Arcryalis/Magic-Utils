package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.api.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallImageUrlsDto
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity

object TestCardData {

    const val cardBackUrl = "cardBackUrl"

    val cardInfoEntity = CardInfoEntity(
        id = "cardId",
        setId = "setId",
        name = "Card Name",
        smallImageUrl = "smallUrl",
        largeImageUrl = "largeUrl",
        oracleText = "Oracle Text",
        isOngoing = true
    )

    val cardInfo = CardInfo(
        name = "Card Name",
        oracleText = "Oracle Text",
        images = CardInfoImageUrls(
            small = "smallUrl",
            large = "largeUrl",
            back = cardBackUrl
        ),
        isOngoing = true
    )

    val cardSetEntity = CardSetEntity(
        id = "setId",
        name = "Set Name"
    )

    val cardSet = CardSet(
        id = "setId",
        name = "Set Name"
    )

    val scryfallCardDto = ScryfallDataDto(
        id = "cardId",
        name = "Card Name",
        imageUris = ScryfallImageUrlsDto(
            small = "smallUrl",
            normal = "normalUrl",
            large = "largeUrl"
        ),
        oracleText = "Oracle Text",
        type = "Enchantment Ongoing",
        setId = "setId",
        setName = "Set Name"
    )
}