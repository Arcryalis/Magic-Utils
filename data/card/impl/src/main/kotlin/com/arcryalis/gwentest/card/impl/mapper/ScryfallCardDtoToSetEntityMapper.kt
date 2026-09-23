package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity

fun ScryfallDataDto.toSetEntity(): CardSetEntity = CardSetEntity(
    id = setId,
    name = setName
)

fun List<ScryfallDataDto>.toDistinctSetEntity() = map {
    it.toSetEntity()
}.distinctBy {
    it.id
}