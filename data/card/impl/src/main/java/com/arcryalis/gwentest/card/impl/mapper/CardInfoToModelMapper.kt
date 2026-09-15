package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity


fun CardInfoEntity.toModel(cardBackUrl: String): CardInfo = CardInfo(
    name = name,
    images = toCardInfoImageUrls(cardBackUrl),
    isOngoing = isOngoing
)

private fun CardInfoEntity.toCardInfoImageUrls(cardBackUrl: String): CardInfoImageUrls = CardInfoImageUrls(
    small = smallImageUrl,
    large = largeImageUrl,
    back = cardBackUrl
)

fun List<CardInfoEntity>.toModel(cardBackUrl: String) = map { it.toModel(cardBackUrl) }