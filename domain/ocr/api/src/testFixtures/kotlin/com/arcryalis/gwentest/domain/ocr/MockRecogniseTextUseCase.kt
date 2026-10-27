package com.arcryalis.gwentest.domain.ocr

import android.graphics.Bitmap

class MockRecogniseTextUseCase(
    results: List<Result<String>> = emptyList()
) : RecogniseTextUseCase {
    private val results = ArrayDeque(results)

    var invocationCount = 0
        private set

    override suspend fun invoke(image: Bitmap): Result<String> {
        invocationCount++
        return results.removeFirstOrNull() ?: Result.success("")
    }
}
