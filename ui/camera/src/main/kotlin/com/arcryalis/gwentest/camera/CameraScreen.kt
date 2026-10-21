package com.arcryalis.gwentest.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
            onCameraError = viewModel::onCameraError,
            onCameraStatusChecked = viewModel::onCameraStatusChecked,
            onPermissionResult = viewModel::onPermissionResult
        )
    }
}

@Composable
fun CameraScreen(
    state: CameraState,
    modifier: Modifier = Modifier,
    onUseManualInputClick: () -> Unit = {},
    onInputChange: (String) -> Unit = {},
    onCameraError: () -> Unit = {},
    onCameraStatusChecked: (hasCamera: Boolean, permissionGranted: Boolean) -> Unit = { _, _ -> },
    onPermissionResult: (granted: Boolean, shouldShowRationale: Boolean) -> Unit = { _, _ -> }
) {
    HandleCameraPermission(
        onCameraStatusChecked = onCameraStatusChecked
    )

    when (state) {
        CameraState.Loading -> LoadingScreen(
            text = null,
            modifier = modifier
        )

        CameraState.PermissionRequired -> CameraPermissionRequiredScreen(
            modifier = modifier,
            onPermissionResult = onPermissionResult,
            onUseManualInputClick = onUseManualInputClick
        )

        is CameraState.Ready -> CameraReadyScreen(
            state = state,
            modifier = modifier,
            onInputChange = onInputChange,
            onCameraError = onCameraError
        )
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
private fun CameraPermissionRequiredScreen(
    modifier: Modifier = Modifier,
    onPermissionResult: (granted: Boolean, shouldShowRationale: Boolean) -> Unit = { _, _ -> },
    onUseManualInputClick: () -> Unit = {}
) {
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

    CameraPermissionRequiredContent(
        modifier = modifier,
        onPermissionRequestClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onUseManualInputClick = onUseManualInputClick
    )
}

@Composable
private fun CameraPermissionRequiredContent(
    modifier: Modifier = Modifier,
    onPermissionRequestClick: () -> Unit = {},
    onUseManualInputClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.camera_permission_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.camera_permission_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = onPermissionRequestClick,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(text = stringResource(R.string.camera_permission_request))
            }

            TextButton(
                onClick = onUseManualInputClick,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = stringResource(R.string.camera_use_manual_input))
            }
        }
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
        CameraScreen(state = CameraState.PermissionRequired)
    }
}

@Preview(showBackground = true)
@Composable
fun CameraReadyPreview() {
    GwenTestTheme {
        CameraScreen(
            state = CameraState.Ready(
                showCamera = true,
                cardName = "Card name"
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
                showCamera = true,
                cardName = ""
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
                showCamera = false,
                cardName = ""
            )
        )
    }
}