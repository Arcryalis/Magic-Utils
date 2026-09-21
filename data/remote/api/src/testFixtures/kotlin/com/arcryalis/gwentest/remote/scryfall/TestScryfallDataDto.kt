package com.arcryalis.gwentest.remote.scryfall

import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallImageUrlsDto

object TestScryfallDataDto {

    val scryfallCardDto = ScryfallDataDto(
        id = "cardId",
        name = "Card Name",
        imageUris = ScryfallImageUrlsDto(
            small = "smallUrl",
            large = "largeUrl"
        ),
        oracleText = "Oracle Text",
        type = "Scheme Ongoing",
        setId = "setId",
        setName = "Set Name"
    )

    val scryfallCardDto2 = ScryfallDataDto(
        id = "cardId 2",
        name = "one more name",
        imageUris = ScryfallImageUrlsDto(
            small = "url small",
            large = "url large"
        ),
        oracleText = "oText",
        type = "scheme",
        setId = "setId the second",
        setName = "further set name"
    )
}