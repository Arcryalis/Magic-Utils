package com.arcryalis.gwentest.camera

import org.junit.Test
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProcessFrameTest {

    private val receivedFrames = mutableListOf<String>()

    @Test
    fun whenExtractThrows_thenFrameSkipped() {
        val processed = processFrame(
            timeoutMillis = LONG_TIMEOUT,
            extract = { error("bad frame") },
            onFrame = ::recordAndComplete,
        )

        assertFalse(processed)
        assertTrue(receivedFrames.isEmpty())
    }

    @Test
    fun whenExtractRunsOutOfMemory_thenFrameSkipped() {
        val processed = processFrame<String>(
            timeoutMillis = LONG_TIMEOUT,
            extract = { throw OutOfMemoryError() },
            onFrame = ::recordAndComplete,
        )

        assertFalse(processed)
        assertTrue(receivedFrames.isEmpty())
    }

    @Test
    fun whenExtractReturnsNull_thenFrameSkipped() {
        val processed = processFrame<String>(
            timeoutMillis = LONG_TIMEOUT,
            extract = { null },
            onFrame = ::recordAndComplete,
        )

        assertFalse(processed)
        assertTrue(receivedFrames.isEmpty())
    }

    @Test
    fun whenProcessedSynchronously_thenTrue() {
        val processed = processFrame(
            timeoutMillis = LONG_TIMEOUT,
            extract = { "frame" },
            onFrame = ::recordAndComplete,
        )

        assertTrue(processed)
        assertEquals(listOf("frame"), receivedFrames)
    }

    @Test
    fun whenProcessedOnAnotherThread_thenTrue() {
        val processed = processFrame(
            timeoutMillis = LONG_TIMEOUT,
            extract = { "frame" },
            onFrame = { _, onProcessed -> thread { onProcessed() } },
        )

        assertTrue(processed)
    }

    @Test
    fun whenNeverProcessed_thenFalseAfterTimeout() {
        val processed = processFrame(
            timeoutMillis = SHORT_TIMEOUT,
            extract = { "frame" },
            onFrame = { frame, _ -> receivedFrames += frame },
        )

        assertFalse(processed)
        assertEquals(listOf("frame"), receivedFrames)
    }

    private fun recordAndComplete(frame: String, onProcessed: () -> Unit) {
        receivedFrames += frame
        onProcessed()
    }

    private companion object {
        const val LONG_TIMEOUT = 5_000L
        const val SHORT_TIMEOUT = 50L
    }
}
