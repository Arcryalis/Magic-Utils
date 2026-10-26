package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.domain.card.AutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.ocr.RecogniseTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RECENT_RESULTS_SIZE = 5
private const val REQUIRED_MATCHES = 3
private const val MIN_QUERY_LENGTH = 2

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val recogniseText: RecogniseTextUseCase,
    private val autocompleteCardNames: AutocompleteCardNamesUseCase,
) : ViewModel() {

    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val deviceHasCamera = MutableStateFlow<Boolean?>(null)
    private val manualOverride = MutableStateFlow(false)
    private val hasCameraError = MutableStateFlow(false)

    private val cardName = MutableStateFlow("")
    private val isScanning = MutableStateFlow(false)
    private val results = MutableStateFlow<CameraResultsState>(CameraResultsState.Idle)
    private val showMinLengthError = MutableStateFlow(false)

    private var autocompleteJob: Job? = null
    private var scanId = 0
    private val recentResults = ArrayDeque<String>()
    private val whitespace = Regex("\\s+")

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

    private val inputState = combine(
        cardName,
        showMinLengthError,
        results
    ) { cardName, showMinLengthError, results ->
        CameraInputState(
            cardName = cardName,
            results = results,
            canSubmit = cardName.trim().length >= MIN_QUERY_LENGTH &&
                    results != CameraResultsState.Loading,
            showMinLengthError = showMinLengthError
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraInputState())

    val state: StateFlow<CameraState> = combine(
        showPermissionRequest,
        includeCameraSection,
        inputState,
        isScanning
    ) { showPermission, showCamera, input, isScanning ->
        if (showPermission) {
            CameraState.PermissionRequired
        } else {
            CameraState.Ready(
                showCamera = showCamera,
                cardName = input.cardName,
                isScanning = isScanning,
                results = input.results,
                canSubmit = input.canSubmit,
                showMinLengthError = input.showMinLengthError
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
        isScanning.value = false
    }

    fun onInputChange(value: String) {
        cardName.value = value
        showMinLengthError.value = false
        isScanning.value = false
    }

    fun onScanClick() {
        scanId++
        recentResults.clear()
        autocompleteJob?.cancel()
        results.value = CameraResultsState.Idle
        isScanning.value = true
    }

    fun onSubmitClick() {
        if (results.value == CameraResultsState.Loading) return

        val query = cardName.value.trim()
        if (query.length < MIN_QUERY_LENGTH) {
            showMinLengthError.value = true
            return
        }

        results.value = CameraResultsState.Loading
        autocompleteJob = viewModelScope.launch {
            results.value = autocompleteCardNames(query).fold(
                onSuccess = { names ->
                    if (names.isEmpty()) CameraResultsState.Empty else CameraResultsState.Results(names)
                },
                onFailure = { CameraResultsState.Error }
            )
        }
    }

    fun onFrame(image: Bitmap, onProcessed: () -> Unit = {}) {
        if (!isScanning.value) {
            onProcessed()
            return
        }
        viewModelScope.launch {
            try {
                val frameScanId = scanId
                val result = recogniseText(image)
                if (!isScanning.value || frameScanId != scanId) return@launch
                result
                    .onSuccess(::onTextRecognised)
                    .onFailure { onRecognitionFailed() }
            } finally {
                onProcessed()
            }
        }
    }

    private fun onRecognitionFailed() {
        isScanning.value = false
        recentResults.clear()
    }

    private fun onTextRecognised(text: String) {
        val normalised = text.trim().replace(whitespace, " ")
        if (normalised.isEmpty()) return

        recentResults.addLast(normalised)
        if (recentResults.size > RECENT_RESULTS_SIZE) {
            recentResults.removeFirst()
        }

        if (recentResults.count { it == normalised } >= REQUIRED_MATCHES) {
            cardName.value = normalised
            showMinLengthError.value = false
            isScanning.value = false
            recentResults.clear()
        }
    }
}

private data class CameraInputState(
    val cardName: String = "",
    val results: CameraResultsState = CameraResultsState.Idle,
    val canSubmit: Boolean = false,
    val showMinLengthError: Boolean = false,
)
