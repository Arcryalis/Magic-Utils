package com.arcryalis.gwentest.card.impl

import java.net.URI
import java.net.URISyntaxException

private const val ID_PRODUCT = "idProduct"

internal fun String.toCardmarketProductUrl(): String? {
    val uri = try {
        URI(this)
    } catch (_: URISyntaxException) {
        return null
    }
    if (uri.scheme != "https" || uri.host == null) return null

    val idProduct = uri.rawQuery
        ?.split("&")
        ?.map { it.substringBefore("=") to it.substringAfter("=", "") }
        ?.firstOrNull { (key, value) -> key == ID_PRODUCT && value.isNotBlank() }
        ?.second
        ?: return null

    return "https://${uri.rawAuthority}${uri.rawPath.orEmpty()}?$ID_PRODUCT=$idProduct"
}
