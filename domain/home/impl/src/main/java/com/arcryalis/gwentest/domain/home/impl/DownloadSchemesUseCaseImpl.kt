package com.arcryalis.gwentest.domain.home.impl

import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.domain.home.DownloadSchemesUseCase
import javax.inject.Inject

class DownloadSchemesUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository
): DownloadSchemesUseCase {
    override suspend fun invoke(): Boolean = cardRepository.downloadSchemes()
}