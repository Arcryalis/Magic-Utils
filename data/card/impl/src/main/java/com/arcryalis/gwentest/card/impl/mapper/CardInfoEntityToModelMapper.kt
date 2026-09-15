package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity

fun CardInfoEntity.toModel(): CardInfo = CardInfo(
    name = name,
    imageUrl = smallImageUrl,
    isOngoing = isOngoing
)

fun List<CardInfoEntity>.toModel() = map { it.toModel() }
