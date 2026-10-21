package com.arcryalis.gwentest.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.camera.core.CameraState as CameraXState

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val deviceHasCamera = MutableStateFlow<Boolean?>(null)
    private val manualOverride = MutableStateFlow(false)
    private val cardName = MutableStateFlow("")
    private val surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)

    private val showPermissionRequest = combine(
        deviceHasCamera,
        hasPermission,
        manualOverride
    ) { hasCamera, permission, override ->
        if (hasCamera == true) {
            if (permission != true && !override) {
                true
            } else {
                false
            }
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val includeCameraSection = combine(
        deviceHasCamera,
        hasPermission
    ) { hasCamera, permission ->
        hasCamera == true && permission == true
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val state: StateFlow<CameraState> = combine(
        showPermissionRequest,
        includeCameraSection,
        cardName,
        surfaceRequest
    ) { showPermission, showCamera, cardName, request ->
        if (showPermission) {
            CameraState.PermissionRequired
        } else {
            CameraState.Ready(
                showCamera = showCamera,
                surfaceRequest = if (showCamera) request else null,
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

    fun onInputChange(value: String) {
        cardName.value = value
    }

    suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequest.value = request }
        }
        val (cameraProvider, camera) = try {
            val provider = ProcessCameraProvider.awaitInstance(context)
            provider to provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            manualOverride.value = true
            return
        }

        val cameraStateObserver = Observer<CameraXState> { cameraState ->
            if (cameraState.error?.isUnusable() == true) {
                manualOverride.value = true
            }
        }
        camera.cameraInfo.cameraState.observeForever(cameraStateObserver)

        try {
            awaitCancellation()
        } finally {
            camera.cameraInfo.cameraState.removeObserver(cameraStateObserver)
            cameraProvider.unbindAll()
            surfaceRequest.value = null
        }
    }

    private fun CameraXState.StateError.isUnusable(): Boolean =
        type == CameraXState.ErrorType.CRITICAL ||
                code == CameraXState.ERROR_CAMERA_IN_USE ||
                code == CameraXState.ERROR_MAX_CAMERAS_IN_USE
}
