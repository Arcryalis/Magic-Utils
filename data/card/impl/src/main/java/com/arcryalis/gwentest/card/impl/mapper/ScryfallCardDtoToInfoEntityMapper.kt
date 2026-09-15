package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.api.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity

private const val TYPE_ONGOING = "ongoing"

fun ScryfallDataDto.toInfoEntity(): CardInfoEntity = CardInfoEntity(
    id = id,
    setId = setId,
    name = name,
    smallImageUrl = imageUris.small,
    oracleText = oracleText,
    isOngoing = type.lowercase().contains(TYPE_ONGOING)
)

fun List<ScryfallDataDto>.toInfoEntity() = map { it.toInfoEntity() }