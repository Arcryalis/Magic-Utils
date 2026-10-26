package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.domain.ocr.MockRecogniseTextUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CameraViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var recogniseText: MockRecogniseTextUseCase

    private lateinit var sut: CameraViewModel

    private fun setupSut(results: List<Result<String>> = emptyList()) {
        recogniseText = MockRecogniseTextUseCase(results)
        sut = CameraViewModel(recogniseText)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }
    }

    @Test
    fun whenNoCamera_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.showCamera)
    }

    @Test
    fun whenCameraWithoutPermission_thenStateIsPermissionRequired() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)

        assertIs<CameraState.PermissionRequired>(sut.state.value)
    }

    @Test
    fun whenCameraWithPermission_thenStateIsReadyWithCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertTrue(state.showCamera)
    }

    @Test
    fun whenUseManualInputClicked_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.showCamera)
    }

    @Test
    fun whenPermissionPermanentlyDenied_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onPermissionResult(granted = false, shouldShowRationale = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.showCamera)
    }

    @Test
    fun whenPermissionDeniedWithRationale_thenStateIsPermissionRequired() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onPermissionResult(granted = false, shouldShowRationale = true)

        assertIs<CameraState.PermissionRequired>(sut.state.value)
    }

    @Test
    fun whenPermissionGrantedOnResumeInManualMode_thenStateIsReadyWithCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertTrue(state.showCamera)
    }

    @Test
    fun whenCameraError_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onCameraError()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.showCamera)
    }

    @Test
    fun whenInputChanged_thenStateHasInput() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Card")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Card", state.cardName)
    }

    @Test
    fun whenNewViewModelCreated_thenManualInputIsCleared() {
        setupSut()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()

        setupSut()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)

        assertIs<CameraState.PermissionRequired>(sut.state.value)
    }

    @Test
    fun whenScreenOpened_thenNotScanning() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenScanClicked_thenScanningStarts() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenSameTextRecognisedThreeTimes_thenCardNameSetAndScanningPaused() {
        setupSut(
            listOf(
                Result.success("Geralt  of Rivia"),
                Result.success(" Geralt of Rivia "),
                Result.success("Geralt\nof Rivia")
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt of Rivia", state.cardName)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenResultsDiffer_thenStillScanning() {
        setupSut(
            listOf(
                Result.success("Geralt"),
                Result.success("Yennefer"),
                Result.success("Ciri")
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("", state.cardName)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenBlankResult_thenIgnored() {
        setupSut(
            listOf(
                Result.success(" "),
                Result.success("\n"),
                Result.success("")
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(3, recogniseText.invocationCount)
        assertEquals("", state.cardName)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenFrameWhileNotScanning_thenNotRecognised() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onFrame(createFrame())

        assertEquals(0, recogniseText.invocationCount)
    }

    @Test
    fun whenUserTypes_thenScanningPaused() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onInputChange("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt", state.cardName)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenRecognitionFails_thenScanningPausedAndCameraShown() {
        setupSut(listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onFrame(createFrame())

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertTrue(state.showCamera)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenScanClickedAfterFailure_thenScanningResumes() {
        setupSut(listOf(Result.failure(IllegalStateException()), Result.success("Geralt")))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onFrame(createFrame())
        sut.onScanClick()
        sut.onFrame(createFrame())

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(2, recogniseText.invocationCount)
        assertTrue(state.showCamera)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenFrameRecognised_thenProcessedCallbackInvoked() {
        setupSut(listOf(Result.success("Geralt")))
        var processed = false

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onFrame(createFrame()) { processed = true }

        assertTrue(processed)
    }

    @Test
    fun whenFrameWhileNotScanning_thenProcessedCallbackInvoked() {
        setupSut()
        var processed = false

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onFrame(createFrame()) { processed = true }

        assertTrue(processed)
    }

    private fun createFrame(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}
