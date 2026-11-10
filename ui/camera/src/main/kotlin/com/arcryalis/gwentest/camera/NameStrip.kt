package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

internal object NameStrip {
    const val WIDTH_FRACTION = 0.85f
    const val HEIGHT_FRACTION = 0.15f
}

private const val FRAME_TIMEOUT_MILLIS = 10_000L

internal fun nameStripAnalyzer(onFrame: (Bitmap, () -> Unit) -> Unit) = ImageAnalysis.Analyzer { imageProxy ->
    processFrame(
        timeoutMillis = FRAME_TIMEOUT_MILLIS,
        extract = { imageProxy.use { it.toNameStripBitmap() } },
        onFrame = onFrame,
    )
}

internal fun <T : Any> processFrame(
    timeoutMillis: Long,
    extract: () -> T?,
    onFrame: (T, () -> Unit) -> Unit,
): Boolean {
    val frame = try {
        extract()
    } catch (_: Exception) {
        null
    } catch (_: OutOfMemoryError) {
        null
    }
    if (frame == null) return false

    val processed = CountDownLatch(1)
    onFrame(frame) { processed.countDown() }
    return processed.await(timeoutMillis, TimeUnit.MILLISECONDS)
}

internal fun visibleRect(bitmapSize: IntSize, imageSize: IntSize, cropRect: IntRect): IntRect = if (bitmapSize == imageSize) {
    cropRect
} else {
    IntRect(IntOffset.Zero, bitmapSize)
}

internal fun nameStripRect(cropRect: IntRect, rotationDegrees: Int): IntRect? {
    if (cropRect.width <= 0 || cropRect.height <= 0) return null

    val isSideways = rotationDegrees % 180 != 0
    val uprightWidth = if (isSideways) cropRect.height else cropRect.width
    val uprightHeight = if (isSideways) cropRect.width else cropRect.height

    val stripWidth = (uprightWidth * NameStrip.WIDTH_FRACTION).roundToInt().coerceIn(1, uprightWidth)
    val stripHeight = (uprightHeight * NameStrip.HEIGHT_FRACTION).roundToInt().coerceIn(1, uprightHeight)

    val width = if (isSideways) stripHeight else stripWidth
    val height = if (isSideways) stripWidth else stripHeight

    return IntRect(
        offset = IntOffset(
            x = cropRect.left + (cropRect.width - width) / 2,
            y = cropRect.top + (cropRect.height - height) / 2,
        ),
        size = IntSize(width, height),
    )
}

private fun ImageProxy.toNameStripBitmap(): Bitmap? {
    val rotation = imageInfo.rotationDegrees
    val source = toBitmap()
    val visible = visibleRect(
        bitmapSize = IntSize(source.width, source.height),
        imageSize = IntSize(width, height),
        cropRect = IntRect(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom),
    )
    val strip = nameStripRect(visible, rotation)
    if (strip == null) {
        source.recycle()
        return null
    }
    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }

    val result = Bitmap.createBitmap(
        source,
        strip.left,
        strip.top,
        strip.width,
        strip.height,
        matrix,
        true,
    )
    if (result !== source) source.recycle()
    return result
}
