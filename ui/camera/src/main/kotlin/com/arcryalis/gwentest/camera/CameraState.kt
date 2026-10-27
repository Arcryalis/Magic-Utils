package com.arcryalis.gwentest.camera

sealed interface CameraState {
    data object Loading : CameraState
    data class Ready(
        val camera: CameraMode,
        val cardName: String,
        val isScanning: Boolean,
        val results: CameraResultsState = CameraResultsState.Idle,
        val canSubmit: Boolean = false,
        val showMinLengthError: Boolean = false,
        val lookup: CameraLookupState = CameraLookupState.Idle,
    ) : CameraState
}

enum class CameraMode {
    Manual,
    PermissionRequired,
    Available
}

sealed interface CameraLookupState {
    data object Idle : CameraLookupState
    data class Loading(val cardName: String) : CameraLookupState
    data class Open(val url: String) : CameraLookupState
    data object Failed : CameraLookupState
}

sealed interface CameraResultsState {
    data object Idle : CameraResultsState
    data object Loading : CameraResultsState
    data object Empty : CameraResultsState
    data object Error : CameraResultsState
    data class Results(val names: List<String>) : CameraResultsState
}
