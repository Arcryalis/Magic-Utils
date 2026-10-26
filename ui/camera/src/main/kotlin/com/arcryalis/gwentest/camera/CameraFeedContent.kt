package com.arcryalis.gwentest.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt
import androidx.camera.core.CameraState as CameraXState

internal object NameStrip {
    const val WIDTH_FRACTION = 0.85f
    const val HEIGHT_FRACTION = 0.12f
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
    modifier: Modifier = Modifier,
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onCameraError: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCameraError by rememberUpdatedState(onCameraError)
    val currentOnFrame by rememberUpdatedState(onFrame)
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var frameAnalysis by remember { mutableStateOf<FrameAnalysis?>(null) }
    var viewfinderSize by remember { mutableStateOf(IntSize.Zero) }

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            val strip = imageProxy.use { it.toNameStripBitmap(viewfinderSize) } ?: return@Analyzer
            val processed = CountDownLatch(1)
            currentOnFrame(strip) { processed.countDown() }
            processed.await(FRAME_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        bindToCamera(
            context = context.applicationContext,
            lifecycleOwner = lifecycleOwner,
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

        NameStripOverlay(
            modifier = Modifier.fillMaxSize()
        )
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
        Box(
            modifier = Modifier
                .weight(marginWeight)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = stringResource(R.string.camera_name_strip_hint),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(NameStrip.HEIGHT_FRACTION)
                .fillMaxWidth(NameStrip.WIDTH_FRACTION)
                .border(2.dp, Color.White)
        )
        Spacer(modifier = Modifier.weight(marginWeight))
    }
}

private fun ImageProxy.toNameStripBitmap(viewfinderSize: IntSize): Bitmap? {
    if (viewfinderSize.width == 0 || viewfinderSize.height == 0) return null

    val rotation = imageInfo.rotationDegrees
    val source = toBitmap()
    val isSideways = rotation % 180 != 0
    val uprightWidth = if (isSideways) source.height else source.width
    val uprightHeight = if (isSideways) source.width else source.height

    val scale = max(
        viewfinderSize.width.toFloat() / uprightWidth,
        viewfinderSize.height.toFloat() / uprightHeight
    )
    val stripWidth = (viewfinderSize.width / scale * NameStrip.WIDTH_FRACTION)
        .roundToInt()
        .coerceIn(1, uprightWidth)
    val stripHeight = (viewfinderSize.height / scale * NameStrip.HEIGHT_FRACTION)
        .roundToInt()
        .coerceIn(1, uprightHeight)

    val cropWidth = if (isSideways) stripHeight else stripWidth
    val cropHeight = if (isSideways) stripWidth else stripHeight
    val matrix = Matrix().apply { postRotate(rotation.toFloat()) }

    val strip = Bitmap.createBitmap(
        source,
        (source.width - cropWidth) / 2,
        (source.height - cropHeight) / 2,
        cropWidth,
        cropHeight,
        matrix,
        true
    )
    if (strip !== source) source.recycle()
    return strip
}

private suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
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
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()
    val analysisExecutor = Executors.newSingleThreadExecutor()

    val (cameraProvider, camera) = try {
        val provider = ProcessCameraProvider.awaitInstance(context)
        provider to provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis
        )
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
        cameraProvider.unbindAll()
        imageAnalysis.clearAnalyzer()
        analysisExecutor.shutdown()
        onSurfaceRequest(null)
    }
}

private fun CameraXState.StateError.isUnusable(): Boolean =
    type == CameraXState.ErrorType.CRITICAL ||
            code == CameraXState.ERROR_CAMERA_IN_USE ||
            code == CameraXState.ERROR_MAX_CAMERAS_IN_USE
