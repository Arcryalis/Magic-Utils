package com.arcryalis.gwentest.domain.ocr

import android.graphics.Bitmap

interface RecogniseTextUseCase {
    suspend operator fun invoke(image: Bitmap): Result<String>
}
