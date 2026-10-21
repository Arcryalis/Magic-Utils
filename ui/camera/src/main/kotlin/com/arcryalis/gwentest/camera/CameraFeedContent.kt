package com.arcryalis.gwentest.camera

import android.content.Context
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import androidx.camera.core.CameraState as CameraXState

private const val SCANNING_AREA_WIDTH_FRACTION = 0.8f
private const val SCANNING_AREA_HEIGHT_FRACTION = 0.6f

@Composable
fun CameraFeedContent(
    modifier: Modifier = Modifier,
    onCameraError: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCameraError by rememberUpdatedState(onCameraError)
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    LaunchedEffect(lifecycleOwner) {
        bindToCamera(
            context = context.applicationContext,
            lifecycleOwner = lifecycleOwner,
            onSurfaceRequest = { surfaceRequest = it },
            onCameraError = { currentOnCameraError() }
        )
    }

    Box(
        modifier = modifier.clipToBounds()
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        ScanningAreaOverlay(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ScanningAreaOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(SCANNING_AREA_WIDTH_FRACTION)
            .fillMaxHeight(SCANNING_AREA_HEIGHT_FRACTION)
            .border(2.dp, Color.White)
    )
}

private suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onSurfaceRequest: (SurfaceRequest?) -> Unit,
    onCameraError: () -> Unit
) {
    val preview = Preview.Builder().build().apply {
        setSurfaceProvider { request -> onSurfaceRequest(request) }
    }
    val (cameraProvider, camera) = try {
        val provider = ProcessCameraProvider.awaitInstance(context)
        provider to provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview
        )
    } catch (_: Exception) {
        onCameraError()
        return
    }

    val cameraStateObserver = Observer<CameraXState> { cameraState ->
        if (cameraState.error?.isUnusable() == true) {
            onCameraError()
        }
    }
    camera.cameraInfo.cameraState.observe(lifecycleOwner, cameraStateObserver)

    try {
        awaitCancellation()
    } finally {
        camera.cameraInfo.cameraState.removeObserver(cameraStateObserver)
        cameraProvider.unbindAll()
        onSurfaceRequest(null)
    }
}

private fun CameraXState.StateError.isUnusable(): Boolean =
    type == CameraXState.ErrorType.CRITICAL ||
            code == CameraXState.ERROR_CAMERA_IN_USE ||
            code == CameraXState.ERROR_MAX_CAMERAS_IN_USE
