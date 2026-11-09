package com.arcryalis.gwentest.camera

import androidx.compose.ui.unit.IntRect
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NameStripRectTest {

    private val landscapeBuffer = IntRect(0, 0, 1280, 720)

    @Test
    fun whenNoRotation_thenStripCentredInCrop() {
        assertEquals(IntRect(96, 306, 1184, 414), nameStripRect(landscapeBuffer, 0))
    }

    @Test
    fun whenRotated180_thenSameAsNoRotation() {
        assertEquals(nameStripRect(landscapeBuffer, 0), nameStripRect(landscapeBuffer, 180))
    }

    @Test
    fun whenRotated90_thenStripIsTallInBuffer() {
        assertEquals(IntRect(544, 54, 736, 666), nameStripRect(landscapeBuffer, 90))
    }

    @Test
    fun whenRotated270_thenSameAs90() {
        assertEquals(nameStripRect(landscapeBuffer, 90), nameStripRect(landscapeBuffer, 270))
    }

    @Test
    fun whenCropOffset_thenStripCentredInCrop() {
        assertEquals(IntRect(175, 305, 1025, 395), nameStripRect(IntRect(100, 50, 1100, 650), 0))
    }

    @Test
    fun whenCropTiny_thenStripAtLeastOnePixel() {
        val strip = assertNotNull(nameStripRect(IntRect(0, 0, 2, 2), 0))

        assertEquals(2, strip.width)
        assertEquals(1, strip.height)
    }

    @Test
    fun whenCropHasZeroWidth_thenNull() {
        assertNull(nameStripRect(IntRect(10, 0, 10, 720), 0))
    }

    @Test
    fun whenCropHasZeroHeight_thenNull() {
        assertNull(nameStripRect(IntRect(0, 10, 1280, 10), 90))
    }
}
