package com.arcryalis.gwentest.camera

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import androidx.camera.core.CameraState as CameraXState

class CameraErrorTest {

    @Test
    fun whenCritical_thenUnusable() {
        assertTrue(
            isUnusableCameraError(CameraXState.ErrorType.CRITICAL, CameraXState.ERROR_CAMERA_FATAL_ERROR)
        )
    }

    @Test
    fun whenCameraInUse_thenUnusable() {
        assertTrue(
            isUnusableCameraError(CameraXState.ErrorType.RECOVERABLE, CameraXState.ERROR_CAMERA_IN_USE)
        )
    }

    @Test
    fun whenMaxCamerasInUse_thenUnusable() {
        assertTrue(
            isUnusableCameraError(CameraXState.ErrorType.RECOVERABLE, CameraXState.ERROR_MAX_CAMERAS_IN_USE)
        )
    }

    @Test
    fun whenOtherRecoverableError_thenUsable() {
        assertFalse(
            isUnusableCameraError(CameraXState.ErrorType.RECOVERABLE, CameraXState.ERROR_STREAM_CONFIG)
        )
    }
}
