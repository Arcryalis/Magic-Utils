package com.arcryalis.gwentest.domain.home

interface DownloadSchemesUseCase {
    suspend operator fun invoke(): Boolean
}