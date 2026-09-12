package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallCardDto

fun ScryfallCardDto.toEntity(setId: String): CardInfoEntity = CardInfoEntity(
    id = id,
    setId = setId
)