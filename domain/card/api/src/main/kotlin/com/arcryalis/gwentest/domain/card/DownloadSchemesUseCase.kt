package com.arcryalis.gwentest.domain.card

interface DownloadSchemesUseCase {
    suspend operator fun invoke(): Boolean
}