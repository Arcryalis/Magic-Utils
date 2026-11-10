package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallDataDto

fun ScryfallDataDto.toSetEntity(): CardSetEntity = CardSetEntity(
    id = setId,
    name = setName,
)

fun List<ScryfallDataDto>.toDistinctSetEntity() = map {
    it.toSetEntity()
}.distinctBy {
    it.id
}
