package com.arcryalis.gwentest.camera

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcryalis.gwentest.core.LoadingScreen
import com.arcryalis.gwentest.core.NavigationScaffold
import com.arcryalis.gwentest.core.theme.GwenTestTheme

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    NavigationScaffold(
        modifier = modifier,
        onNavigateBack = onNavigateBack,
    ) { scaffoldModifier ->
        CameraScreen(
            state = state.value,
            modifier = scaffoldModifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.navigationBars)
                .imePadding(),
            onUseManualInputClick = viewModel::onUseManualInputClick,
            onInputChange = viewModel::onInputChange,
            onSubmitClick = viewModel::onSubmitClick,
            onOpenCardmarketClick = viewModel::onOpenCardmarketClick,
            onLookupHandled = viewModel::onLookupHandled,
            onUrlOpenFailed = viewModel::onUrlOpenFailed,
            onScreenStopped = viewModel::onScreenStopped,
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
    onUseManualInputClick: () -> Unit = {},
    onInputChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onOpenCardmarketClick: (String) -> Unit = {},
    onLookupHandled: () -> Unit = {},
    onUrlOpenFailed: () -> Unit = {},
    onScreenStopped: () -> Unit = {},
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
            CardLookupEffect(
                lookup = state.lookup,
                onLookupHandled = onLookupHandled,
                onUrlOpenFailed = onUrlOpenFailed,
                onScreenStopped = onScreenStopped
            )
            CameraReadyScreen(
                state = state,
                modifier = modifier,
                onInputChange = onInputChange,
                onSubmitClick = onSubmitClick,
                onOpenCardmarketClick = onOpenCardmarketClick,
                onCameraError = onCameraError,
                onFrame = onFrame,
                onScanClick = onScanClick,
                onPermissionRequestClick = rememberCameraPermissionRequest(onPermissionResult),
                onUseManualInputClick = onUseManualInputClick
            )
        }
    }
}

@Composable
private fun CardLookupEffect(
    lookup: CameraLookupState,
    onLookupHandled: () -> Unit = {},
    onUrlOpenFailed: () -> Unit = {},
    onScreenStopped: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        if (activity?.isChangingConfigurations != true) {
            onScreenStopped()
        }
    }

    LaunchedEffect(lookup) {
        when (lookup) {
            is CameraLookupState.Open -> {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, lookup.url.toUri()))
                    onLookupHandled()
                } catch (_: ActivityNotFoundException) {
                    onUrlOpenFailed()
                }
            }

            CameraLookupState.Failed -> {
                Toast.makeText(context, R.string.camera_card_open_error, Toast.LENGTH_SHORT).show()
                onLookupHandled()
            }

            else -> Unit
        }
    }
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

@Composable
private fun rememberCameraPermissionRequest(
    onPermissionResult: (granted: Boolean, shouldShowRationale: Boolean) -> Unit
): () -> Unit {
    val activity = LocalActivity.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onPermissionResult(
            granted,
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: true
        )
    }

    return { permissionLauncher.launch(Manifest.permission.CAMERA) }
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
                cardName = "",
                isScanning = false
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
                camera = CameraMode.Available,
                cardName = "Card name",
                isScanning = false
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
                camera = CameraMode.Available,
                cardName = "",
                isScanning = true
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
                camera = CameraMode.Available,
                cardName = "Thal",
                isScanning = false,
                results = CameraResultsState.Results(
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
                cardName = "",
                isScanning = false
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
                camera = CameraMode.Available,
                cardName = "Thal",
                isScanning = false,
                results = CameraResultsState.Results(
                    listOf("Thallid", "Thalakos Seer", "Thalia, the Survivor")
                ),
                canSubmit = true,
                lookup = CameraLookupState.Loading("Thalakos Seer")
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
                camera = CameraMode.Available,
                cardName = "Thal",
                isScanning = false,
                results = CameraResultsState.Loading
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
                isScanning = false,
                results = CameraResultsState.Empty,
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
                isScanning = false,
                results = CameraResultsState.Error,
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
                isScanning = false,
                showMinLengthError = true
            )
        )
    }
}
