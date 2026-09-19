package com.arcryalis.gwentest.domain.card

class MockDownloadSchemesUseCase(
    private val result: Boolean = true
) : DownloadSchemesUseCase {
    override suspend fun invoke(): Boolean = result
}
