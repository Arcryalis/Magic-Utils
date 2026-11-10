package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.domain.card.MockAutocompleteCardNamesUseCase
import com.arcryalis.gwentest.domain.card.MockGetCardmarketUrlUseCase
import com.arcryalis.gwentest.domain.ocr.MockRecogniseTextUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private const val CARDMARKET_URL = "https://www.cardmarket.com/en/Magic/Products?idProduct=1"

@RunWith(RobolectricTestRunner::class)
class CameraViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

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
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
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
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
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
    fun whenCameraErrorThenStatusChecked_thenCameraAvailable() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onCameraError()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
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
        assertEquals(CameraMode.PermissionRequired, state.camera)
    }

    @Test
    fun whenPermissionLostWhileScanning_thenScanningResetOnRegrant() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = false)
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
    }

    @Test
    fun whenManualClickedWhileScanning_thenScanningStops() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onUseManualInputClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Manual, state.camera)
    }

    @Test
    fun whenScreenOpened_thenNotScanning() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
    }

    @Test
    fun whenScanClicked_thenScanningStarts() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    @Test
    fun whenStatusCheckedWhileScanning_thenStillScanning() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    @Test
    fun whenSameTextRecognisedThreeTimes_thenCardNameSetAndScanningPaused() {
        setupSut(
            listOf(
                Result.success("Geralt  of Rivia 12"),
                Result.success(" Geralt of Rivia 12 "),
                Result.success("Geralt\nof Rivia 12")
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt of Rivia", state.cardName)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
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
        assertEquals(3, recogniseText.invocationCount)
        assertEquals("", state.cardName)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    @Test
    fun whenRescanned_thenEarlierMatchesForgotten() {
        setupSut(List(4) { Result.success("Geralt") })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(2) { sut.onFrame(createFrame()) }
        sut.onScanClick()
        repeat(2) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("", state.cardName)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
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
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
    }

    @Test
    fun whenInputFocused_thenScanningPaused() {
        setupSut()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onInputFocused()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
    }

    @Test
    fun whenRecognitionFailsOnce_thenStillScanning() {
        setupSut(listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        sut.onFrame(createFrame())

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    @Test
    fun whenRecognitionFailsThreeTimes_thenScanningPausedAndCameraShown() {
        setupSut(List(3) { Result.failure(IllegalStateException()) })

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = false), state.camera)
    }

    @Test
    fun whenFailureThenSuccess_thenFailureCountReset() {
        setupSut(
            listOf(
                Result.failure(IllegalStateException()),
                Result.failure(IllegalStateException()),
                Result.success("Geralt"),
                Result.failure(IllegalStateException()),
                Result.failure(IllegalStateException())
            )
        )

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(5) { sut.onFrame(createFrame()) }

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
    }

    @Test
    fun whenScanClickedAfterFailures_thenScanningResumes() {
        setupSut(List(3) { Result.failure<String>(IllegalStateException()) } + Result.success("Geralt"))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onScanClick()
        repeat(3) { sut.onFrame(createFrame()) }
        sut.onScanClick()
        sut.onFrame(createFrame())

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(4, recogniseText.invocationCount)
        assertEquals(CameraMode.Available(isScanning = true), state.camera)
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
    fun whenSubmitClicked_thenSearchResultsShown() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt", "Geralt of Rivia"))))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange(" Geralt ")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals("Geralt", autocompleteCardNames.lastQuery)
        assertEquals(CardSearchResultState.Results(listOf("Geralt", "Geralt of Rivia")), state.searchResults)
    }

    @Test
    fun whenSubmitReturnsEmpty_thenNoResultsState() {
        setupSut(autocompleteResults = listOf(Result.success(emptyList())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Zzzz")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CardSearchResultState.NoResults, state.searchResults)
    }

    @Test
    fun whenSubmitFails_thenErrorState() {
        setupSut(autocompleteResults = listOf(Result.failure(IllegalStateException())))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CardSearchResultState.Error, state.searchResults)
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
        assertEquals(CardSearchResultState.Empty, state.searchResults)
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
    fun whenInputChangedAfterSearchResults_thenSearchResultsKept() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt"))))

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onInputChange("Yen")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CardSearchResultState.Results(listOf("Geralt")), state.searchResults)
    }

    @Test
    fun whenSubmitInProgress_thenLoadingAndCannotSubmit() {
        setupSut(autocompleteGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onSubmitClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CardSearchResultState.Loading, state.searchResults)
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
        assertEquals(CardSearchResultState.Empty, state.searchResults)
    }

    @Test
    fun whenScanClicked_thenSearchResultsCleared() {
        setupSut(autocompleteResults = listOf(Result.success(listOf("Geralt"))))

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onInputChange("Geralt")
        sut.onSubmitClick()
        sut.onScanClick()

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(CardSearchResultState.Empty, state.searchResults)
    }

    @Test
    fun whenScanClickedDuringRequest_thenSearchResultsCleared() {
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
        assertEquals(CardSearchResultState.Empty, state.searchResults)
    }

    @Test
    fun whenResultClicked_thenStoreLinkLoadingForName() {
        setupSut(cardmarketGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenLinkClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(StoreLinkState.Loading("Geralt"), state.storeLink)
        assertEquals("Geralt", getCardmarketUrl.lastCardName)
    }

    @Test
    fun whenStoreLinkSucceeds_thenOpenEventSentAndIdle() = runTest {
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)))
        val events = collectStoreLinkEvents()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenLinkClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(listOf(StoreLinkEvent.Open(CARDMARKET_URL)), events)
        assertEquals(StoreLinkState.Idle, state.storeLink)
    }

    @Test
    fun whenStoreLinkFails_thenFailedEventSentAndIdle() = runTest {
        setupSut(cardmarketResults = listOf(Result.failure(IllegalStateException())))
        val events = collectStoreLinkEvents()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenLinkClick("Geralt")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(listOf<StoreLinkEvent>(StoreLinkEvent.Failed), events)
        assertEquals(StoreLinkState.Idle, state.storeLink)
    }

    @Test
    fun whenResultClickedDuringStoreLink_thenIgnored() {
        setupSut(cardmarketGate = CompletableDeferred())

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onOpenLinkClick("Geralt")
        sut.onOpenLinkClick("Yennefer")

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(1, getCardmarketUrl.invocationCount)
        assertEquals(StoreLinkState.Loading("Geralt"), state.storeLink)
    }

    @Test
    fun whenScanClickedDuringStoreLink_thenStoreLinkCancelled() = runTest {
        val gate = CompletableDeferred<Unit>()
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)), cardmarketGate = gate)
        val events = collectStoreLinkEvents()

        sut.onCameraStatusChecked(hasCamera = true, permissionGranted = true)
        sut.onOpenLinkClick("Geralt")
        sut.onScanClick()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(StoreLinkState.Idle, state.storeLink)
        assertEquals(emptyList(), events)
    }

    @Test
    fun whenSubmitClickedDuringStoreLink_thenStoreLinkCancelled() = runTest {
        val gate = CompletableDeferred<Unit>()
        setupSut(cardmarketResults = listOf(Result.success(CARDMARKET_URL)), cardmarketGate = gate)
        val events = collectStoreLinkEvents()

        sut.onCameraStatusChecked(hasCamera = false, permissionGranted = false)
        sut.onInputChange("Geralt")
        sut.onOpenLinkClick("Geralt")
        sut.onSubmitClick()
        gate.complete(Unit)

        val state = assertIs<CameraState.Ready>(sut.state.value)
        assertEquals(StoreLinkState.Idle, state.storeLink)
        assertEquals(emptyList(), events)
    }

    private fun TestScope.collectStoreLinkEvents(): List<StoreLinkEvent> {
        val events = mutableListOf<StoreLinkEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.storeLinkEvents.toList(events)
        }
        return events
    }

    private fun createFrame(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}
