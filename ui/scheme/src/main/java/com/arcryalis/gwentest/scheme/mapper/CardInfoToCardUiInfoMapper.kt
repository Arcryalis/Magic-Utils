package com.arcryalis.gwentest.scheme.mapper

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls
import com.arcryalis.gwentest.scheme.CardUiImageUrls
import com.arcryalis.gwentest.scheme.CardUiInfo

fun CardInfo.toUiModel(): CardUiInfo = CardUiInfo(
    name = name,
    oracleText = oracleText,
    images = images.toUiModel(),
    isOngoing = isOngoing
)

fun List<CardInfo>.toUiModel(): List<CardUiInfo> = this.map { it.toUiModel() }

fun CardInfoImageUrls.toUiModel(): CardUiImageUrls = CardUiImageUrls(
    small = small,
    large = large,
    back = back
)