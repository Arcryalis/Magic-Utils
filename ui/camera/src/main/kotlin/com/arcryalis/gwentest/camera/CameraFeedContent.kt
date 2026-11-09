package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

@Composable
internal fun CameraFeedContent(
    mode: CameraFeedMode,
    modifier: Modifier = Modifier,
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onCameraError: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onPermissionRequestClick: () -> Unit = {},
    onUseManualInputClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val isResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    val displayRotation = rememberDisplayRotation()
    val currentOnCameraError by rememberUpdatedState(onCameraError)
    val currentOnFrame by rememberUpdatedState(onFrame)
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var viewfinderSize by remember { mutableStateOf(IntSize.Zero) }

    val isScanning = mode == CameraFeedMode.Scanning
    val canBind = isScanning && isResumed && viewfinderSize != IntSize.Zero
    val analyzer = remember {
        nameStripAnalyzer { bitmap, onProcessed -> currentOnFrame(bitmap, onProcessed) }
    }

    LaunchedEffect(lifecycleOwner, canBind, viewfinderSize, displayRotation) {
        if (!canBind) return@LaunchedEffect
        bindToCamera(
            context = context.applicationContext,
            lifecycleOwner = lifecycleOwner,
            viewfinderSize = viewfinderSize,
            displayRotation = displayRotation,
            analyzer = analyzer,
            onSurfaceRequest = { surfaceRequest = it },
            onCameraError = { currentOnCameraError() }
        )
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { viewfinderSize = it }
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        when (mode) {
            CameraFeedMode.Scanning -> NameStripOverlay(
                modifier = Modifier.fillMaxSize()
            )

            is CameraFeedMode.Paused,
            CameraFeedMode.PermissionRequired -> PausedOverlay(
                mode = mode,
                modifier = Modifier.fillMaxSize(),
                onScanClick = onScanClick,
                onPermissionRequestClick = onPermissionRequestClick,
                onUseManualInputClick = onUseManualInputClick
            )
        }
    }
}

@Composable
private fun rememberDisplayRotation(): Int {
    val view = LocalView.current
    var rotation by remember(view) {
        mutableIntStateOf(view.display?.rotation ?: Surface.ROTATION_0)
    }

    DisposableEffect(view) {
        val displayManager = view.context.getSystemService(DisplayManager::class.java)
        val listener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(displayId: Int) = Unit
            override fun onDisplayRemoved(displayId: Int) = Unit
            override fun onDisplayChanged(displayId: Int) {
                val display = view.display ?: return
                if (display.displayId == displayId) rotation = display.rotation
            }
        }
        displayManager.registerDisplayListener(listener, null)
        view.display?.let { rotation = it.rotation }
        onDispose { displayManager.unregisterDisplayListener(listener) }
    }

    return rotation
}

@Composable
private fun PausedOverlay(
    mode: CameraFeedMode,
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {},
    onPermissionRequestClick: () -> Unit = {},
    onUseManualInputClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        when (mode) {
            CameraFeedMode.PermissionRequired -> Button(onClick = onPermissionRequestClick) {
                Text(text = stringResource(R.string.camera_permission_request))
            }

            is CameraFeedMode.Paused -> Button(
                onClick = {
                    focusManager.clearFocus()
                    onScanClick()
                }
            ) {
                Text(
                    text = stringResource(
                        if (mode.hasCardName) R.string.camera_rescan else R.string.camera_scan
                    )
                )
            }

            CameraFeedMode.Scanning -> Unit
        }

        IconButton(
            onClick = onUseManualInputClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.camera_use_manual_input),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NameStripOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(NameStrip.WIDTH_FRACTION)
                .fillMaxHeight(NameStrip.HEIGHT_FRACTION)
                .border(2.dp, MaterialTheme.colorScheme.primary)
        )
    }
}
