package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.domain.card.AutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.card.GetCardmarketUrlUseCase
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
private const val MIN_SCAN_LENGTH = 3

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val recogniseText: RecogniseTextUseCase,
    private val autocompleteCardNames: AutocompleteCardNamesUseCase,
    private val getCardmarketUrl: GetCardmarketUrlUseCase,
) : ViewModel() {

    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val deviceHasCamera = MutableStateFlow<Boolean?>(null)
    private val manualMode = MutableStateFlow(false)
    private val hasCameraError = MutableStateFlow(false)

    private val cardName = MutableStateFlow("")
    private val isScanning = MutableStateFlow(false)
    private val results = MutableStateFlow<CameraResultsState>(CameraResultsState.Idle)
    private val showMinLengthError = MutableStateFlow(false)
    private val lookup = MutableStateFlow<CameraLookupState>(CameraLookupState.Idle)

    private var autocompleteJob: Job? = null
    private var lookupJob: Job? = null
    private var scanId = 0
    private val recentResults = ArrayDeque<String>()
    private val whitespace = Regex("\\s+")
    private val trailingNumber = Regex(" \\d+$")

    private val cameraMode = combine(
        deviceHasCamera,
        hasPermission,
        manualMode,
        hasCameraError
    ) { hasCamera, permission, manual, error ->
        when {
            hasCamera == null -> null
            !hasCamera || manual || error -> CameraMode.Manual
            permission != true -> CameraMode.PermissionRequired
            else -> CameraMode.Available
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val inputState = combine(
        cardName,
        showMinLengthError,
        results,
        lookup
    ) { cardName, showMinLengthError, results, lookup ->
        CameraInputState(
            cardName = cardName,
            results = results,
            canSubmit = cardName.trim().length >= MIN_QUERY_LENGTH &&
                    results != CameraResultsState.Loading,
            showMinLengthError = showMinLengthError,
            lookup = lookup
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraInputState())

    val state: StateFlow<CameraState> = combine(
        cameraMode,
        inputState,
        isScanning
    ) { camera, input, isScanning ->
        if (camera == null) {
            CameraState.Loading
        } else {
            CameraState.Ready(
                camera = camera,
                cardName = input.cardName,
                isScanning = isScanning,
                results = input.results,
                canSubmit = input.canSubmit,
                showMinLengthError = input.showMinLengthError,
                lookup = input.lookup
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraState.Loading)

    fun onCameraStatusChecked(hasCamera: Boolean, permissionGranted: Boolean) {
        if (permissionGranted && hasPermission.value == false) {
            manualMode.value = false
        }

        deviceHasCamera.value = hasCamera
        hasPermission.value = permissionGranted
    }

    fun onPermissionResult(granted: Boolean, shouldShowRationale: Boolean) {
        hasPermission.value = granted

        if (!granted && !shouldShowRationale) {
            manualMode.value = true
        }
    }

    fun onUseManualInputClick() {
        manualMode.value = true
        isScanning.value = false
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
        if (cameraMode.value != CameraMode.Available) return

        scanId++
        recentResults.clear()
        autocompleteJob?.cancel()
        results.value = CameraResultsState.Idle
        cancelLookup()
        isScanning.value = true
    }

    fun onSubmitClick() {
        if (results.value == CameraResultsState.Loading) return

        val query = cardName.value.trim()
        if (query.length < MIN_QUERY_LENGTH) {
            showMinLengthError.value = true
            return
        }

        cancelLookup()
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

    fun onOpenCardmarketClick(cardName: String) {
        if (lookup.value is CameraLookupState.Loading) return

        lookup.value = CameraLookupState.Loading(cardName)
        lookupJob = viewModelScope.launch {
            lookup.value = getCardmarketUrl(cardName).fold(
                onSuccess = { url -> CameraLookupState.Open(url) },
                onFailure = { CameraLookupState.Failed }
            )
        }
    }

    fun onLookupHandled() {
        lookup.value = CameraLookupState.Idle
    }

    fun onUrlOpenFailed() {
        lookup.value = CameraLookupState.Failed
    }

    fun onScreenStopped() {
        val current = lookup.value
        if (current is CameraLookupState.Loading || current is CameraLookupState.Open) {
            cancelLookup()
        }
    }

    private fun cancelLookup() {
        lookupJob?.cancel()
        lookup.value = CameraLookupState.Idle
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
        val stripped = normalised.replace(trailingNumber, "")
        if (stripped.length < MIN_SCAN_LENGTH) return

        recentResults.addLast(normalised)
        if (recentResults.size > RECENT_RESULTS_SIZE) {
            recentResults.removeFirst()
        }

        if (recentResults.count { it == normalised } >= REQUIRED_MATCHES) {
            cardName.value = stripped
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
    val lookup: CameraLookupState = CameraLookupState.Idle,
)
