package com.arcryalis.gwentest.camera

import android.content.Context
import android.util.Rational
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.awaitCancellation
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException
import androidx.camera.core.CameraState as CameraXState

private val ANALYSIS_TARGET_SIZE = Size(1280, 720)

internal suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    viewfinderSize: IntSize,
    displayRotation: Int,
    analyzer: ImageAnalysis.Analyzer,
    onSurfaceRequest: (SurfaceRequest?) -> Unit,
    onCameraError: () -> Unit
) {
    val preview = buildPreview(displayRotation, onSurfaceRequest)
    val imageAnalysis = buildImageAnalysis(displayRotation)
    val useCaseGroup = buildUseCaseGroup(viewfinderSize, displayRotation, preview, imageAnalysis)

    val (cameraProvider, camera) = try {
        val provider = ProcessCameraProvider.awaitInstance(context)
        provider to provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            useCaseGroup
        )
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        onCameraError()
        return
    }

    val analysisExecutor = Executors.newSingleThreadExecutor()
    imageAnalysis.setAnalyzer(analysisExecutor, analyzer)

    val cameraStateObserver = Observer<CameraXState> { cameraState ->
        val error = cameraState.error
        if (error != null && isUnusableCameraError(error.type, error.code)) {
            onCameraError()
        }
    }
    camera.cameraInfo.cameraState.observe(lifecycleOwner, cameraStateObserver)

    try {
        awaitCancellation()
    } finally {
        camera.cameraInfo.cameraState.removeObserver(cameraStateObserver)
        cameraProvider.unbind(preview, imageAnalysis)
        imageAnalysis.clearAnalyzer()
        analysisExecutor.shutdown()
        onSurfaceRequest(null)
    }
}

internal fun isUnusableCameraError(type: CameraXState.ErrorType, code: Int): Boolean =
    type == CameraXState.ErrorType.CRITICAL ||
            code == CameraXState.ERROR_CAMERA_IN_USE ||
            code == CameraXState.ERROR_MAX_CAMERAS_IN_USE

private fun buildPreview(
    displayRotation: Int,
    onSurfaceRequest: (SurfaceRequest?) -> Unit
): Preview =
    Preview.Builder()
        .setResolutionSelector(ratio16x9Selector().build())
        .setTargetRotation(displayRotation)
        .build()
        .apply {
            setSurfaceProvider { request -> onSurfaceRequest(request) }
        }

private fun buildImageAnalysis(displayRotation: Int): ImageAnalysis =
    ImageAnalysis.Builder()
        .setResolutionSelector(
            ratio16x9Selector()
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

private fun buildUseCaseGroup(
    viewfinderSize: IntSize,
    displayRotation: Int,
    preview: Preview,
    imageAnalysis: ImageAnalysis
): UseCaseGroup {
    val viewPort = ViewPort.Builder(
        Rational(viewfinderSize.width, viewfinderSize.height),
        displayRotation
    )
        .setScaleType(ViewPort.FILL_CENTER)
        .build()
    return UseCaseGroup.Builder()
        .setViewPort(viewPort)
        .addUseCase(preview)
        .addUseCase(imageAnalysis)
        .build()
}

private fun ratio16x9Selector(): ResolutionSelector.Builder =
    ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
