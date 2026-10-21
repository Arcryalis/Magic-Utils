package com.arcryalis.gwentest.camera

import com.arcryalis.gwentest.core.BaseDispatchRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var sut: CameraViewModel

    private fun setupSut() {
        sut = CameraViewModel()
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }
    }

    @Test
    fun whenInitialized_thenStateIsLoading() {
        setupSut()

        assertIs<CameraState.Loading>(sut.state.value)
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
}
