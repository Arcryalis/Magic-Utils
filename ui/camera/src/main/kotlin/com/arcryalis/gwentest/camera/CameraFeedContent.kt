package com.arcryalis.gwentest.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Rational
import android.util.Size
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import kotlinx.coroutines.awaitCancellation
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt
import androidx.camera.core.CameraState as CameraXState

internal object NameStrip {
    const val WIDTH_FRACTION = 0.85f
    const val HEIGHT_FRACTION = 0.15f
}

private val ANALYSIS_TARGET_SIZE = Size(1280, 720)
private const val FRAME_TIMEOUT_SECONDS = 10L

private class FrameAnalysis(
    val imageAnalysis: ImageAnalysis,
    val executor: ExecutorService
)

@Composable
fun CameraFeedContent(
    isScanning: Boolean,
    hasCardName: Boolean,
    permissionRequired: Boolean,
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
    val displayRotation = LocalView.current.display?.rotation ?: Surface.ROTATION_0
    val currentOnCameraError by rememberUpdatedState(onCameraError)
    val currentOnFrame by rememberUpdatedState(onFrame)
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var frameAnalysis by remember { mutableStateOf<FrameAnalysis?>(null) }
    var viewfinderSize by remember { mutableStateOf(IntSize.Zero) }

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            val strip = imageProxy.use { it.toNameStripBitmap() }
            val processed = CountDownLatch(1)
            currentOnFrame(strip) { processed.countDown() }
            processed.await(FRAME_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }
    }

    LaunchedEffect(lifecycleOwner, isScanning, isResumed, viewfinderSize, displayRotation) {
        if (!isScanning || !isResumed || viewfinderSize == IntSize.Zero) return@LaunchedEffect
        bindToCamera(
            context = context.applicationContext,
            lifecycleOwner = lifecycleOwner,
            viewfinderSize = viewfinderSize,
            displayRotation = displayRotation,
            onSurfaceRequest = { surfaceRequest = it },
            onFrameAnalysis = { frameAnalysis = it },
            onCameraError = { currentOnCameraError() }
        )
    }

    DisposableEffect(frameAnalysis, isScanning) {
        val analysis = frameAnalysis
        if (analysis != null && isScanning) {
            analysis.imageAnalysis.setAnalyzer(analysis.executor, analyzer)
        }
        onDispose {
            analysis?.imageAnalysis?.clearAnalyzer()
        }
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

        if (isScanning) {
            NameStripOverlay(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            PausedOverlay(
                hasCardName = hasCardName,
                permissionRequired = permissionRequired,
                modifier = Modifier.fillMaxSize(),
                onScanClick = onScanClick,
                onPermissionRequestClick = onPermissionRequestClick,
                onUseManualInputClick = onUseManualInputClick
            )
        }
    }
}

@Composable
private fun PausedOverlay(
    hasCardName: Boolean,
    permissionRequired: Boolean,
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
        if (permissionRequired) {
            Button(onClick = onPermissionRequestClick) {
                Text(text = stringResource(R.string.camera_permission_request))
            }
        } else {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onScanClick()
                }
            ) {
                Text(
                    text = stringResource(
                        if (hasCardName) R.string.camera_rescan else R.string.camera_scan
                    )
                )
            }
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
    val marginWeight = (1f - NameStrip.HEIGHT_FRACTION) / 2

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(marginWeight))
        Box(
            modifier = Modifier
                .weight(NameStrip.HEIGHT_FRACTION)
                .fillMaxWidth(NameStrip.WIDTH_FRACTION)
                .border(2.dp, MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.weight(marginWeight))
    }
}

internal fun nameStripRect(cropRect: IntRect, rotationDegrees: Int): IntRect {
    val isSideways = rotationDegrees % 180 != 0
    val uprightWidth = if (isSideways) cropRect.height else cropRect.width
    val uprightHeight = if (isSideways) cropRect.width else cropRect.height

    val stripWidth = (uprightWidth * NameStrip.WIDTH_FRACTION).roundToInt().coerceIn(1, uprightWidth)
    val stripHeight = (uprightHeight * NameStrip.HEIGHT_FRACTION).roundToInt().coerceIn(1, uprightHeight)

    val width = if (isSideways) stripHeight else stripWidth
    val height = if (isSideways) stripWidth else stripHeight

    return IntRect(
        offset = IntOffset(
            x = cropRect.left + (cropRect.width - width) / 2,
            y = cropRect.top + (cropRect.height - height) / 2
        ),
        size = IntSize(width, height)
    )
}

private fun ImageProxy.toNameStripBitmap(): Bitmap {
    val rotation = imageInfo.rotationDegrees
    val source = toBitmap()
    val isFullBuffer = source.width == width && source.height == height
    val visible = if (isFullBuffer) {
        IntRect(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom)
    } else {
        IntRect(0, 0, source.width, source.height)
    }
    val strip = nameStripRect(visible, rotation)
    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }

    val result = Bitmap.createBitmap(
        source,
        strip.left,
        strip.top,
        strip.width,
        strip.height,
        matrix,
        true
    )
    if (result !== source) source.recycle()
    return result
}

private suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    viewfinderSize: IntSize,
    displayRotation: Int,
    onSurfaceRequest: (SurfaceRequest?) -> Unit,
    onFrameAnalysis: (FrameAnalysis?) -> Unit,
    onCameraError: () -> Unit
) {
    val preview = Preview.Builder()
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
        )
        .setTargetRotation(displayRotation)
        .build()
        .apply {
            setSurfaceProvider { request -> onSurfaceRequest(request) }
        }
    val imageAnalysis = ImageAnalysis.Builder()
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .setResolutionStrategy(
                    ResolutionStrategy(
                        ANALYSIS_TARGET_SIZE,
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER
                    )
                )
                .build()
        )
        .setTargetRotation(displayRotation)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
    val viewPort = ViewPort.Builder(
        Rational(viewfinderSize.width, viewfinderSize.height),
        displayRotation
    )
        .setScaleType(ViewPort.FILL_CENTER)
        .build()
    val useCaseGroup = UseCaseGroup.Builder()
        .setViewPort(viewPort)
        .addUseCase(preview)
        .addUseCase(imageAnalysis)
        .build()
    val analysisExecutor = Executors.newSingleThreadExecutor()

    val (cameraProvider, camera) = try {
        val provider = ProcessCameraProvider.awaitInstance(context)
        provider to provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            useCaseGroup
        )
    } catch (e: CancellationException) {
        analysisExecutor.shutdown()
        throw e
    } catch (_: Exception) {
        analysisExecutor.shutdown()
        onCameraError()
        return
    }

    val cameraStateObserver = Observer<CameraXState> { cameraState ->
        if (cameraState.error?.isUnusable() == true) {
            onCameraError()
        }
    }
    camera.cameraInfo.cameraState.observe(lifecycleOwner, cameraStateObserver)
    onFrameAnalysis(FrameAnalysis(imageAnalysis, analysisExecutor))

    try {
        awaitCancellation()
    } finally {
        onFrameAnalysis(null)
        camera.cameraInfo.cameraState.removeObserver(cameraStateObserver)
        cameraProvider.unbind(preview, imageAnalysis)
        imageAnalysis.clearAnalyzer()
        analysisExecutor.shutdown()
        onSurfaceRequest(null)
    }
}

private fun CameraXState.StateError.isUnusable(): Boolean =
    type == CameraXState.ErrorType.CRITICAL ||
            code == CameraXState.ERROR_CAMERA_IN_USE ||
            code == CameraXState.ERROR_MAX_CAMERAS_IN_USE
