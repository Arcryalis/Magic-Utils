package com.arcryalis.gwentest.camera

sealed interface CameraState {
    data object Loading : CameraState
    data object PermissionRequired : CameraState
    data class Ready(
        val showCamera: Boolean,
        val cardName: String,
        val isScanning: Boolean,
    ) : CameraState
}
