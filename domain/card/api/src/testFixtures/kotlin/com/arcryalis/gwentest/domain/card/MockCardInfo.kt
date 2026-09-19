package com.arcryalis.gwentest.domain.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

object MockCardInfo {

    val cardInfo = CardInfo(
        name = "c.name",
        oracleText = "o.text",
        images = CardInfoImageUrls(
            small = "https://small.url",
            large = "https://large.url",
            back = "https://back.url"
        ),
        isOngoing = false,
    )

    val cardInfo2 = CardInfo(
        name = "c2.name",
        oracleText = "o2.text",
        images = CardInfoImageUrls(
            small = "https://too-small.url",
            large = "https://too-large.url",
            back = "https://too-back.url"
        ),
        isOngoing = true,
    )
}