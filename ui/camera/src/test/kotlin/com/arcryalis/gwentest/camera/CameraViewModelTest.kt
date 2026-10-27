package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.domain.card.MockAutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardmarketUrlUseCase
import com.arcryalis.gwentest.domain.ocr.MockRecogniseTextUseCase
import kotlinx.coroutines.CompletableDeferred
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

private const val CARDMARKET_URL = "https://www.cardmarket.com/en/Magic/Products?idProduct=1"

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CameraViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var recogniseText: MockRecogniseTextUseCase
    private lateinit var autocompleteCardNames: MockAutocompleteCardNamesUseCase
    private lateinit var getCardmarketUrl: MockGetCardmarketUrlUseCase

    private lateinit var sut: CameraViewModel

    private fun setupSut(
        results: List<Result<String>> = emptyList(),
        autocompleteResults: List<Result<List<String>>> = emptyList(),
        autocompleteGate: CompletableDeferred<Unit>? = null,
        cardmarketResults: List<Result<String>> = emptyList(),
        cardmarketGate: CompletableDeferred<Unit>? = null
    ) {
        recogniseText = MockRecogniseTextUseCase(results)
        autocompleteCardNames = MockAutocompleteCardNamesUseCase(autocompleteResults, autocompleteGate)
        getCardmarketUrl = MockGetCardmarketUrlUseCase(cardmarketResults, cardmarketGate)
        sut = CameraViewModel(recogniseText, autocompleteCardNames, getCardmarketUrl)
        testScope.backgroundScope.launch(UnconfinedTestDispatcher()) { sut.state.collect {} }
    }

    @Test
    fun whenStatusNotChecked_thenLoading() {
        setupSut()

        assertIs<CameraState.Loading>(sut.state.value)
    }

    @Test
    fun whenNoCamera_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenCameraWithoutPermission_thenStateIsPermissionRequired() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.PermissionRequired, state.camera)
    }

    @Test
    fun whenCameraWithPermission_thenStateIsReadyWithCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available, state.camera)
    }

    @Test
    fun whenUseManualInputClicked_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenPermissionPermanentlyDenied_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onPermissionResult(granted = false, shouldShowRationale = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenPermissionDeniedWithRationale_thenStateIsPermissionRequired() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onPermissionResult(granted = false, shouldShowRationale = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.PermissionRequired, state.camera)
    }

    @Test
    fun whenPermissionGrantedOnResumeInManualMode_thenStateIsReadyWithCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available, state.camera)
    }

    @Test
    fun whenCameraError_thenStateIsReadyWithoutCamera() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onCameraError()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
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
    fun whenManualClickedWithPermission_thenManualMode() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onUseManualInputClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenResumedInManualModeWithPermission_thenStaysManual() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onUseManualInputClick()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenScanClickedWithoutPermission_thenNotScanning() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onScanClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenManualClickedWhileScanning_thenScanningStops() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onUseManualInputClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenNewViewModelCreated_thenManualInputIsCleared() {
        setupSut()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onUseManualInputClick()

        setupSut()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.PermissionRequired, state.camera)
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
    fun whenShortResults_thenIgnored() {
        setupSut(
            listOf(
                Result.success("G"),
                Result.success("Ge"),
                Result.success("Ge"),
                Result.success("Ge")
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(4) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("", state.cardName)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenStrippedResultTooShort_thenIgnored() {
        setupSut(results = List(3) { Result.success("Ox 12") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("", state.cardName)
        assertTrue(state.isScanning)
    }

    @Test
    fun whenResultEndsWithSpaceAndNumber_thenNumberStripped() {
        setupSut(results = List(3) { Result.success("Geralt of Rivia 12") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt of Rivia", state.cardName)
        assertFalse(state.isScanning)
    }

    @Test
    fun whenResultEndsWithNumberWithoutSpace_thenKept() {
        setupSut(results = List(3) { Result.success("Card42") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Card42", state.cardName)
    }

    @Test
    fun whenTrailingNumbersDiffer_thenNoMatch() {
        setupSut(
            listOf(
                Result.success("Geralt 12"),
                Result.success("Geralt 13"),
                Result.success("Geralt 14")
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
        assertEquals(CameraMode.Available, state.camera)
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
        assertEquals(CameraMode.Available, state.camera)
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

    @Test
    fun whenSubmitClicked_thenResultsShown() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt", "Geralt of Rivia"))))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange(" Geralt ")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt", autocompleteCardNames.lastQuery)
        assertEquals(CameraResultsState.Results(listOf("Geralt", "Geralt of Rivia")), state.results)
    }

    @Test
    fun whenSubmitReturnsEmpty_thenEmptyState() {
        setupSut(autocompleteResults = listOf(Result.success(emptyList())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Zzzz")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Empty, state.results)
    }

    @Test
    fun whenSubmitFails_thenErrorState() {
        setupSut(autocompleteResults = listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Error, state.results)
    }

    @Test
    fun whenNameUnderTwoChars_thenCannotSubmit() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange(" G ")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.canSubmit)
    }

    @Test
    fun whenNameTwoChars_thenCanSubmit() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Ge")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertTrue(state.canSubmit)
    }

    @Test
    fun whenSubmitWithShortName_thenNotRequestedAndErrorShown() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("G")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(0, autocompleteCardNames.invocationCount)
        assertTrue(state.showMinLengthError)
        assertEquals(CameraResultsState.Idle, state.results)
    }

    @Test
    fun whenInputChangedAfterBlockedSubmit_thenErrorCleared() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("G")
        sut.onSubmitClick()
        sut.onInputChange("Ge")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertFalse(state.showMinLengthError)
    }

    @Test
    fun whenInputChangedAfterResults_thenResultsKept() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt"))))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onInputChange("Yen")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Results(listOf("Geralt")), state.results)
    }

    @Test
    fun whenSubmitInProgress_thenLoadingAndCannotSubmit() {
        setupSut(autocompleteGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Loading, state.results)
        assertFalse(state.canSubmit)
        assertEquals(1, autocompleteCardNames.invocationCount)
    }

    @Test
    fun whenScanSucceeds_thenNotSubmitted() {
        setupSut(results = List(3) { Result.success("Geralt") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt", state.cardName)
        assertEquals(0, autocompleteCardNames.invocationCount)
        assertEquals(CameraResultsState.Idle, state.results)
    }

    @Test
    fun whenScanClicked_thenResultsCleared() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt"))))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onScanClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Idle, state.results)
    }

    @Test
    fun whenScanClickedDuringRequest_thenResultsCleared() {
        val gate = CompletableDeferred<Unit>()
        setupSut(
            autocompleteResults = listOf(Result.success(listOf("Geralt"))),
            autocompleteGate = gate
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onScanClick()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraResultsState.Idle, state.results)
    }

    @Test
    fun whenResultClicked_thenLookupLoadingForName() {
        setupSut(cardmarketGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Loading("Geralt"), state.lookup)
        assertEquals("Geralt", getCardmarketUrl.lastCardName)
    }

    @Test
    fun whenLookupSucceeds_thenOpenUrl() {
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Open(CARDMARKET_URL), state.lookup)
    }

    @Test
    fun whenLookupFails_thenFailed() {
        setupSut(cardmarketResults = listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Failed, state.lookup)
    }

    @Test
    fun whenResultClickedDuringLookup_thenIgnored() {
        setupSut(cardmarketGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onOpenCardmarketClick("Yennefer")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(1, getCardmarketUrl.invocationCount)
        assertEquals(CameraLookupState.Loading("Geralt"), state.lookup)
    }

    @Test
    fun whenLookupHandled_thenIdle() {
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onLookupHandled()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Idle, state.lookup)
    }

    @Test
    fun whenUrlOpenFailed_thenFailed() {
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onUrlOpenFailed()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Failed, state.lookup)
    }

    @Test
    fun whenScanClickedDuringLookup_thenLookupCancelled() {
        val gate = CompletableDeferred<Unit>()
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)), cardmarketGate = gate)

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onOpenCardmarketClick("Geralt")
        sut.onScanClick()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Idle, state.lookup)
    }

    @Test
    fun whenSubmitClickedDuringLookup_thenLookupCancelled() {
        val gate = CompletableDeferred<Unit>()
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)), cardmarketGate = gate)

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onOpenCardmarketClick("Geralt")
        sut.onSubmitClick()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Idle, state.lookup)
    }

    @Test
    fun whenScreenStoppedDuringLookup_thenLookupCancelled() {
        val gate = CompletableDeferred<Unit>()
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)), cardmarketGate = gate)

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onScreenStopped()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Idle, state.lookup)
    }

    @Test
    fun whenScreenStoppedWithPendingOpen_thenIdle() {
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onScreenStopped()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Idle, state.lookup)
    }

    @Test
    fun whenScreenStoppedAfterLookupFailed_thenFailureKept() {
        setupSut(cardmarketResults = listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenCardmarketClick("Geralt")
        sut.onScreenStopped()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraLookupState.Failed, state.lookup)
    }

    private fun createFrame(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}
