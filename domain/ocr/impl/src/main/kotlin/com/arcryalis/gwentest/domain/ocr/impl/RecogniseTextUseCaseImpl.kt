package com.arcryalis.gwentest.domain.ocr.impl

import android.graphics.Bitmap
import android.os.Build
import com.arcryalis.gwentest.domain.ocr.RecogniseTextUseCase
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.comparisons.compareBy
import kotlin.comparisons.nullsLast
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RecogniseTextUseCaseImpl @Inject internal constructor(
    private val recognizer: TextRecognizer,
) : RecogniseTextUseCase {

    override suspend fun invoke(image: Bitmap): Result<String> =
        runCatching { recognise(image) }
            .onFailure { if (it is CancellationException) throw it }

    private suspend fun recognise(image: Bitmap): String {
        val source = if (isHardware(image)) {
            image.copy(Bitmap.Config.ARGB_8888, false) ?: error("Could not copy hardware bitmap")
        } else {
            image
        }
        try {
            val text = recognizer.process(InputImage.fromBitmap(source, 0)).await()
            val lines = text.textBlocks
                .flatMap { it.lines }
                .sortedWith(compareBy(nullsLast()) { it.boundingBox?.left })
                .map { RecognisedLine(it.text, it.confidence) }
            return joinLines(lines)
        } finally {
            if (source !== image) source.recycle()
        }
    }

    private fun isHardware(image: Bitmap): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && image.config == Bitmap.Config.HARDWARE
}

private suspend fun Task<Text>.await(): Text = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener(Runnable::run) { continuation.resume(it) }
    addOnFailureListener(Runnable::run) { continuation.resumeWithException(it) }
}
