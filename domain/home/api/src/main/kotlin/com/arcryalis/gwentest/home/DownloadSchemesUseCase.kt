package com.arcryalis.gwentest.home

interface DownloadSchemesUseCase {
    suspend operator fun invoke(): Boolean
}