package com.arcryalis.gwentest.camera

sealed interface CameraState {
    data object Loading : CameraState
    data class Ready(
        val camera: CameraMode,
        val cardName: String,
        val searchResults: CardSearchResultState = CardSearchResultState.Empty,
        val canSubmit: Boolean = false,
        val showMinLengthError: Boolean = false,
        val storeLink: StoreLinkState = StoreLinkState.Idle,
    ) : CameraState
}

sealed interface CameraMode {
    data object Manual : CameraMode
    data object PermissionRequired : CameraMode
    data class Available(val isScanning: Boolean) : CameraMode
}

internal sealed interface CameraFeedMode {
    data object Scanning : CameraFeedMode
    data class Paused(val hasCardName: Boolean) : CameraFeedMode
    data object PermissionRequired : CameraFeedMode
}

internal fun CameraState.Ready.toFeedMode(): CameraFeedMode? = when (val mode = camera) {
    CameraMode.Manual -> null
    CameraMode.PermissionRequired -> CameraFeedMode.PermissionRequired
    is CameraMode.Available -> if (mode.isScanning) {
        CameraFeedMode.Scanning
    } else {
        CameraFeedMode.Paused(hasCardName = cardName.isNotBlank())
    }
}

sealed interface StoreLinkState {
    data object Idle : StoreLinkState
    data class Loading(val cardName: String) : StoreLinkState
}

sealed interface StoreLinkEvent {
    data class Open(val url: String) : StoreLinkEvent
    data object Failed : StoreLinkEvent
}

sealed interface CardSearchResultState {
    data object Empty : CardSearchResultState
    data object Loading : CardSearchResultState
    data object NoResults : CardSearchResultState
    data object Error : CardSearchResultState
    data class Results(val names: List<String>) : CardSearchResultState
}
