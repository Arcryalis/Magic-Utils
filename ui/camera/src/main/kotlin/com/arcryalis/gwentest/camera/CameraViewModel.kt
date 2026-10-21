package com.arcryalis.gwentest.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val deviceHasCamera = MutableStateFlow<Boolean?>(null)
    private val manualOverride = MutableStateFlow(false)
    private val hasCameraError = MutableStateFlow(false)

    private val cardName = MutableStateFlow("")

    private val showPermissionRequest = combine(
        deviceHasCamera,
        hasPermission,
        manualOverride
    ) { hasCamera, permission, override ->
        if (hasCamera == true) {
            permission != true && !override
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val includeCameraSection = combine(
        deviceHasCamera,
        hasPermission,
        hasCameraError
    ) { hasCamera, permission, error ->
        hasCamera == true && permission == true && !error
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val state: StateFlow<CameraState> = combine(
        showPermissionRequest,
        includeCameraSection,
        cardName
    ) { showPermission, showCamera, cardName ->
        if (showPermission) {
            CameraState.PermissionRequired
        } else {
            CameraState.Ready(
                showCamera = showCamera,
                cardName = cardName
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraState.Loading)

    fun onCameraStatusChecked(hasCamera: Boolean, permissionGranted: Boolean) {
        deviceHasCamera.value = hasCamera
        hasPermission.value = permissionGranted

        if (permissionGranted) {
            manualOverride.value = false
        }
    }

    fun onPermissionResult(granted: Boolean, shouldShowRationale: Boolean) {
        hasPermission.value = granted

        if (!granted && !shouldShowRationale) {
            manualOverride.value = true
        }
    }

    fun onUseManualInputClick() {
        manualOverride.value = true
    }

    fun onCameraError() {
        hasCameraError.value = true
    }

    fun onInputChange(value: String) {
        cardName.value = value
    }
}
