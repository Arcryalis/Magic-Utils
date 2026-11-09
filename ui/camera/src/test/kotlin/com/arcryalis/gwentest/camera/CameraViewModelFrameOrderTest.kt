package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.domain.card.MockAutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardmarketUrlUseCase
import com.arcryalis.gwentest.domain.ocr.MockRecogniseTextUseCase
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(RobolectricTestRunner::class)
class CameraViewModelFrameOrderTest {

    private val dispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcher = BaseDispatchRule(dispatcher)

    private lateinit var sut: CameraViewModel

    private fun setupSut(results: List<Result<String>>) {
        sut = CameraViewModel(
            MockRecogniseTextUseCase(results),
            MockAutocompleteCardNamesUseCase(),
            MockGetCardmarketUrlUseCase(),
        )
    }

    @Test
    fun whenFramesQueuedBeforeRescan_thenNotCounted() {
        setupSut(List(3) { Result.success("Geralt") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        dispatcher.scheduler.advanceUntilIdle()
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }
        sut.onScanClick()
        dispatcher.scheduler.advanceUntilIdle()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("", state.cardName)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    private fun createFrame(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}
