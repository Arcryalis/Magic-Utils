package com.arcryalis.gwentest.data.card

import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardInfoImageUrls

object TestCardInfo {

    val info1 = CardInfo(
        name = "c.name",
        oracleText = "o.text",
        images = CardInfoImageUrls(
            small = "https://small.url",
            large = "https://large.url"
        ),
        isOngoing = false,
    )

    val info2 = CardInfo(
        name = "c2.name",
        oracleText = "o2.text",
        images = CardInfoImageUrls(
            small = "https://too-small.url",
            large = "https://too-large.url"
        ),
        isOngoing = true,
    )
}
