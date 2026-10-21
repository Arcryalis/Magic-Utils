package com.arcryalis.gwentest.camera

import androidx.camera.core.SurfaceRequest

sealed interface CameraState {
    data object Loading : CameraState
    data object PermissionRequired : CameraState
    data class Ready(
        val showCamera: Boolean,
        val surfaceRequest: SurfaceRequest?,
        val cardName: String,
    ) : CameraState
}
