package com.arcryalis.gwentest.camera

sealed interface CameraState {
    data object Loading : CameraState
    data object PermissionRequired : CameraState
    data class Ready(
        val showCamera: Boolean,
        val cardName: String,
        val isScanning: Boolean,
        val results: CameraResultsState = CameraResultsState.Idle,
        val canSubmit: Boolean = false,
        val showMinLengthError: Boolean = false,
    ) : CameraState
}

sealed interface CameraResultsState {
    data object Idle : CameraResultsState
    data object Loading : CameraResultsState
    data object Empty : CameraResultsState
    data object Error : CameraResultsState
    data class Results(val names: List<String>) : CameraResultsState
}
