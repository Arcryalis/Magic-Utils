package com.arcryalis.gwentest.camera

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.NavigationScaffold
import com.arcryalis.gwentest.core.rememberPermissionRequest
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavigationScaffold(
        modifier = modifier,
        onNavigateBack = onNavigateBack,
    ) { scaffoldModifier ->
        CameraScreen(
            state = state,
            storeLinkEvents = viewModel.storeLinkEvents,
            modifier = scaffoldModifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.navigationBars)
                .imePadding(),
            onUseManualInputClick = viewModel::onUseManualInputClick,
            onInputChange = viewModel::onInputChange,
            onInputFocused = viewModel::onInputFocused,
            onSubmitClick = viewModel::onSubmitClick,
            onOpenLinkClick = viewModel::onOpenLinkClick,
            onCameraError = viewModel::onCameraError,
            onCameraStatusChecked = viewModel::onCameraStatusChecked,
            onPermissionResult = viewModel::onPermissionResult,
            onFrame = viewModel::onFrame,
            onScanClick = viewModel::onScanClick
        )
    }
}

@Composable
fun CameraScreen(
    state: CameraState,
    modifier: Modifier = Modifier,
    storeLinkEvents: Flow<StoreLinkEvent> = emptyFlow(),
    onUseManualInputClick: () -> Unit = {},
    onInputChange: (String) -> Unit = {},
    onInputFocused: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onOpenLinkClick: (String) -> Unit = {},
    onCameraError: () -> Unit = {},
    onCameraStatusChecked: (hasCamera: Boolean, permissionGranted: Boolean) -> Unit = { _, _ -> },
    onPermissionResult: (granted: Boolean, shouldShowRationale: Boolean) -> Unit = { _, _ -> },
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {}
) {
    HandleCameraPermission(
        onCameraStatusChecked = onCameraStatusChecked
    )

    when (state) {
        CameraState.Loading -> LoadingScreen(
            text = null,
            modifier = modifier
        )

        is CameraState.Ready -> {
            StoreLinkEffect(events = storeLinkEvents)

            CameraReadyScreen(
                state = state,
                modifier = modifier,
                onInputChange = onInputChange,
                onInputFocused = onInputFocused,
                onSubmitClick = onSubmitClick,
                onOpenLinkClick = onOpenLinkClick,
                onCameraError = onCameraError,
                onFrame = onFrame,
                onScanClick = onScanClick,
                onPermissionRequestClick = rememberPermissionRequest(
                    permission = Manifest.permission.CAMERA,
                    onPermissionResult = onPermissionResult
                ),
                onUseManualInputClick = onUseManualInputClick
            )
        }
    }
}

@Composable
private fun StoreLinkEffect(
    events: Flow<StoreLinkEvent>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { event ->
                when (event) {
                    is StoreLinkEvent.Open -> {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                        } catch (_: ActivityNotFoundException) {
                            context.showCardOpenError()
                        }
                    }

                    StoreLinkEvent.Failed -> context.showCardOpenError()
                }
            }
        }
    }
}

private fun Context.showCardOpenError() {
    Toast.makeText(this, R.string.camera_card_open_error, Toast.LENGTH_SHORT).show()
}

@Composable
private fun HandleCameraPermission(
    onCameraStatusChecked: (hasCamera: Boolean, permissionGranted: Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    LifecycleResumeEffect(Unit) {
        onCameraStatusChecked(
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY),
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
        onPauseOrDispose { }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraLoadingPreview() {
    GwenTestTheme {
        CameraScreen(state = CameraState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPermissionRequiredPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.PermissionRequired,
                cardName = ""
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Available(isScanning = false),
                cardName = "Card name"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyScanningPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Available(isScanning = true),
                cardName = ""
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun CameraReadyLandscapePreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Available(isScanning = false),
                cardName = "Thal",
                searchResults = CardSearchResultState.Results(
                    listOf("Thallid", "Thalakos Seer", "Thalia, Guardian of Thraben")
                ),
                canSubmit = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyManualInputPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Manual,
                cardName = ""
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyResultsPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Available(isScanning = false),
                cardName = "Thal",
                searchResults = CardSearchResultState.Results(
                    listOf("Thallid", "Thalakos Seer", "Thalia, the Survivor")
                ),
                canSubmit = true,
                storeLink = StoreLinkState.Loading("Thalakos Seer")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyLoadingResultsPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Available(isScanning = false),
                cardName = "Thal",
                searchResults = CardSearchResultState.Loading
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyEmptyResultsPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Manual,
                cardName = "Zzzz",
                searchResults = CardSearchResultState.NoResults,
                canSubmit = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyErrorResultsPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Manual,
                cardName = "Thal",
                searchResults = CardSearchResultState.Error,
                canSubmit = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyMinLengthErrorPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                camera = CameraMode.Manual,
                cardName = "T",
                showMinLengthError = true
            )
        )
    }
}
