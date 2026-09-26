package com.arcryalis.gwentest.domain.card

interface GetCardBackUrlUseCase {
    suspend operator fun invoke(): String
}
