package com.arcryalis.gwentest.domain.card

interface GetCardmarketUrlUseCase {
    suspend operator fun invoke(cardName: String): Result<String>
}
