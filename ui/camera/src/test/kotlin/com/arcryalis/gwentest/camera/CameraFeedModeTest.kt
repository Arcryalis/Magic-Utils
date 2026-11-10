package com.arcryalis.gwentest.camera

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CameraFeedModeTest {

    @Test
    fun whenManual_thenNull() {
        assertNull(readyState(CameraMode.Manual).toFeedMode())
    }

    @Test
    fun whenPermissionRequired_thenPermissionRequired() {
        assertEquals(
            CameraFeedMode.PermissionRequired,
            readyState(CameraMode.PermissionRequired).toFeedMode(),
        )
    }

    @Test
    fun whenScanning_thenScanning() {
        assertEquals(
            CameraFeedMode.Scanning,
            readyState(CameraMode.Available(isScanning = true), cardName = "Bolt").toFeedMode(),
        )
    }

    @Test
    fun whenNotScanningWithoutName_thenPausedWithoutName() {
        assertEquals(
            CameraFeedMode.Paused(hasCardName = false),
            readyState(CameraMode.Available(isScanning = false)).toFeedMode(),
        )
    }

    @Test
    fun whenNotScanningWithBlankName_thenPausedWithoutName() {
        assertEquals(
            CameraFeedMode.Paused(hasCardName = false),
            readyState(CameraMode.Available(isScanning = false), cardName = "   ").toFeedMode(),
        )
    }

    @Test
    fun whenNotScanningWithName_thenPausedWithName() {
        assertEquals(
            CameraFeedMode.Paused(hasCardName = true),
            readyState(CameraMode.Available(isScanning = false), cardName = "Bolt").toFeedMode(),
        )
    }

    private fun readyState(camera: CameraMode, cardName: String = "") = CameraState.Ready(camera = camera, cardName = cardName)
}
