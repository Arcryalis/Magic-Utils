package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.home.DownloadSchemesUseCase
import javax.inject.Inject

class DownloadSchemesUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
): DownloadSchemesUseCase {
    override suspend fun invoke(): Boolean = cardRepository.downloadSchemes()
}