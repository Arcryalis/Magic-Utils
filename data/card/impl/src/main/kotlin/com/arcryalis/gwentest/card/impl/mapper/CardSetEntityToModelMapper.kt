package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity

fun CardSetEntity.toModel(): CardSet = CardSet(
    id = id,
    name = name,
)

fun List<CardSetEntity>.toModel() = map { it.toModel() }
