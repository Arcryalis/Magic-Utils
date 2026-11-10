package com.arcryalis.gwentest.camera

import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import org.junit.Test
import kotlin.test.assertEquals

class VisibleRectTest {

    private val imageSize = IntSize(1280, 720)
    private val cropRect = IntRect(100, 50, 1100, 650)

    @Test
    fun whenBitmapMatchesImage_thenCropRectUsed() {
        assertEquals(cropRect, visibleRect(imageSize, imageSize, cropRect))
    }

    @Test
    fun whenBitmapAlreadyCropped_thenWholeBitmapUsed() {
        assertEquals(
            IntRect(0, 0, 1000, 600),
            visibleRect(IntSize(1000, 600), imageSize, cropRect)
        )
    }
}
