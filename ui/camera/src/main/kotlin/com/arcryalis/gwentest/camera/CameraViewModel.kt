package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcryalis.gwentest.domain.card.AutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.card.GetCardmarketUrlUseCase
import com.arcryalis.gwentest.domain.ocr.RecogniseTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_QUERY_LENGTH = 2
private const val MAX_CONSECUTIVE_FAILURES = 3

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
    private val isScanning = MutableStateFlow(false)

    private val cardName = MutableStateFlow("")
    private val searchResults = MutableStateFlow<CardSearchResultState>(CardSearchResultState.Empty)
    private val showMinLengthError = MutableStateFlow(false)
    private val storeLink = MutableStateFlow<StoreLinkState>(StoreLinkState.Idle)
    private val _storeLinkEvents = MutableSharedFlow<StoreLinkEvent>()
    val storeLinkEvents: SharedFlow<StoreLinkEvent> = _storeLinkEvents.asSharedFlow()

    private var searchJob: Job? = null
    private var storeLinkJob: Job? = null
    private var scanId = 0
    private var consecutiveFailures = 0
    private val scanTextMatcher = ScanTextMatcher()

    private val cameraMode = combine(
        deviceHasCamera,
        hasPermission,
        manualMode,
        hasCameraError,
        isScanning
    ) { hasCamera, permission, manual, error, scanning ->
        when {
            hasCamera == null -> null
            !hasCamera || manual || error -> CameraMode.Manual
            permission != true -> CameraMode.PermissionRequired
            else -> CameraMode.Available(isScanning = scanning)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val inputState = combine(
        cardName,
        showMinLengthError,
        searchResults,
        storeLink
    ) { cardName, showMinLengthError, searchResults, storeLink ->
        CameraInputState(
            cardName = cardName,
            searchResults = searchResults,
            canSubmit = cardName.trim().length >= MIN_QUERY_LENGTH &&
                    searchResults != CardSearchResultState.Loading,
            showMinLengthError = showMinLengthError,
            storeLink = storeLink
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraInputState())

    val state: StateFlow<CameraState> = combine(
        cameraMode,
        inputState
    ) { camera, input ->
        if (camera == null) {
            CameraState.Loading
        } else {
            CameraState.Ready(
                camera = camera,
                cardName = input.cardName,
                searchResults = input.searchResults,
                canSubmit = input.canSubmit,
                showMinLengthError = input.showMinLengthError,
                storeLink = input.storeLink
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CameraState.Loading)

    fun onCameraStatusChecked(hasCamera: Boolean, permissionGranted: Boolean) {
        if (permissionGranted && hasPermission.value == false) {
            manualMode.value = false
        }
        if (!hasCamera || !permissionGranted) {
            isScanning.value = false
        }

        deviceHasCamera.value = hasCamera
        hasPermission.value = permissionGranted
        hasCameraError.value = false
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

    fun onInputFocused() {
        isScanning.value = false
    }

    fun onScanClick() {
        if (cameraMode.value !is CameraMode.Available) return

        scanId++
        consecutiveFailures = 0
        scanTextMatcher.reset()
        searchJob?.cancel()
        searchResults.value = CardSearchResultState.Empty
        cancelStoreLink()
        isScanning.value = true
    }

    fun onSubmitClick() {
        if (searchResults.value == CardSearchResultState.Loading) return

        val query = cardName.value.trim()
        if (query.length < MIN_QUERY_LENGTH) {
            showMinLengthError.value = true
            return
        }

        cancelStoreLink()
        searchResults.value = CardSearchResultState.Loading
        searchJob = viewModelScope.launch {
            searchResults.value = autocompleteCardNames(query).fold(
                onSuccess = { names ->
                    if (names.isEmpty()) CardSearchResultState.NoResults else CardSearchResultState.Results(names)
                },
                onFailure = { CardSearchResultState.Error }
            )
        }
    }

    fun onOpenLinkClick(cardName: String) {
        if (storeLink.value is StoreLinkState.Loading) return

        storeLink.value = StoreLinkState.Loading(cardName)
        storeLinkJob = viewModelScope.launch {
            val event = getCardmarketUrl(cardName).fold(
                onSuccess = { url -> StoreLinkEvent.Open(url) },
                onFailure = { StoreLinkEvent.Failed }
            )
            storeLink.value = StoreLinkState.Idle
            _storeLinkEvents.emit(event)
        }
    }

    private fun cancelStoreLink() {
        storeLinkJob?.cancel()
        storeLink.value = StoreLinkState.Idle
    }

    fun onFrame(image: Bitmap, onProcessed: () -> Unit = {}) {
        if (!isScanning.value) {
            onProcessed()
            return
        }
        val frameScanId = scanId
        viewModelScope.launch {
            try {
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
        consecutiveFailures++
        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            isScanning.value = false
            consecutiveFailures = 0
            scanTextMatcher.reset()
        }
    }

    private fun onTextRecognised(text: String) {
        consecutiveFailures = 0
        val match = scanTextMatcher.onText(text) ?: return

        cardName.value = match
        showMinLengthError.value = false
        isScanning.value = false
    }
}

private data class CameraInputState(
    val cardName: String = "",
    val searchResults: CardSearchResultState = CardSearchResultState.Empty,
    val canSubmit: Boolean = false,
    val showMinLengthError: Boolean = false,
    val storeLink: StoreLinkState = StoreLinkState.Idle,
)
