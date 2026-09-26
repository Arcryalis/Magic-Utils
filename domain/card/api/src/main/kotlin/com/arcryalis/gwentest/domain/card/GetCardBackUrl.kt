package com.arcryalis.gwentest.domain.card

interface GetCardBackUrl {
    suspend operator fun invoke(): String
}
