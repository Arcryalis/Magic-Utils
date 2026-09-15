package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.card.impl.CardRepositoryImpl.Companion.CARD_BACK_URL
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity


fun CardInfoEntity.toModel(): CardInfo = CardInfo(
    name = name,
    images = toCardInfoImageUrls(),
    isOngoing = isOngoing
)

private fun CardInfoEntity.toCardInfoImageUrls(): CardInfoImageUrls = CardInfoImageUrls(
    small = smallImageUrl,
    large = largeImageUrl,
    back = CARD_BACK_URL
)

fun List<CardInfoEntity>.toModel() = map { it.toModel() }