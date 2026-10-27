package com.arcryalis.gwentest.card.impl

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CardmarketUrlTest {

    @Test
    fun whenScryfallLink_thenOnlyIdProductKept() {
        assertEquals(
            "https://www.cardmarket.com/en/Magic/Products?idProduct=818770",
            "https://www.cardmarket.com/en/Magic/Products?idProduct=818770&referrer=scryfall&utm_campaign=card_prices&utm_medium=text&utm_source=scryfall"
                .toCardmarketProductUrl()
        )
        assertEquals(
            "https://www.cardmarket.com/en/Magic/Products?idProduct=748533",
            "https://www.cardmarket.com/en/Magic/Products?idProduct=748533&referrer=scryfall&utm_campaign=card_prices&utm_medium=text&utm_source=scryfall"
                .toCardmarketProductUrl()
        )
    }

    @Test
    fun whenIdProductNotFirst_thenStillKept() {
        assertEquals(
            "https://www.cardmarket.com/en/Magic/Products?idProduct=818770",
            "https://www.cardmarket.com/en/Magic/Products?referrer=scryfall&idProduct=818770"
                .toCardmarketProductUrl()
        )
    }

    @Test
    fun whenNoIdProduct_thenNull() {
        assertNull(
            "https://www.cardmarket.com/en/Magic/Products?referrer=scryfall".toCardmarketProductUrl()
        )
    }

    @Test
    fun whenEmptyIdProduct_thenNull() {
        assertNull(
            "https://www.cardmarket.com/en/Magic/Products?idProduct=&referrer=scryfall".toCardmarketProductUrl()
        )
    }

    @Test
    fun whenNoQuery_thenNull() {
        assertNull("https://www.cardmarket.com/en/Magic/Products".toCardmarketProductUrl())
    }

    @Test
    fun whenHttpLink_thenNull() {
        assertNull(
            "http://www.cardmarket.com/en/Magic/Products?idProduct=818770".toCardmarketProductUrl()
        )
    }

    @Test
    fun whenMalformedLink_thenNull() {
        assertNull(
            "https://www.cardmarket.com/en/Magic Products?idProduct=818770".toCardmarketProductUrl()
        )
    }

    @Test
    fun whenFragmentPresent_thenDropped() {
        assertEquals(
            "https://www.cardmarket.com/en/Magic/Products?idProduct=818770",
            "https://www.cardmarket.com/en/Magic/Products?idProduct=818770#offers".toCardmarketProductUrl()
        )
    }
}
