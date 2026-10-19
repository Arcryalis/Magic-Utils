package com.arcryalis.gwentest.camera

sealed interface CameraState {
    data object PermissionRequired : CameraState
}
