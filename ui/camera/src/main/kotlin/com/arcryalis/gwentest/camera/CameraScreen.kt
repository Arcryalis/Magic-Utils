package com.arcryalis.gwentest.camera

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcryalis.gwentest.core.NavigationScaffold
import com.arcryalis.gwentest.core.theme.GwenTestTheme

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    NavigationScaffold(
        modifier = modifier,
        onNavigateBack = onNavigateBack,
    ) { scaffoldModifier ->
        CameraScreen(
            state = state.value,
            modifier = scaffoldModifier.fillMaxSize(),
            onEnableClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

@Composable
fun CameraScreen(
    state: CameraState,
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit = {}
) {
    when (state) {
        CameraState.PermissionRequired -> CameraPermissionRequired(
            modifier = modifier,
            onEnableClick = onEnableClick
        )
    }
}

@Composable
private fun CameraPermissionRequired(
    modifier: Modifier = Modifier,
    onEnableClick: () -> Unit = {}
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
                onClick = onEnableClick,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(text = stringResource(R.string.camera_permission_request))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPermissionRequiredPreview() {
    GwenTestTheme {
        CameraScreen(state = CameraState.PermissionRequired)
    }
}
