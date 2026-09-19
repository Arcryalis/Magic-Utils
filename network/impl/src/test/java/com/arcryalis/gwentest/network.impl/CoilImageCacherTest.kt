package com.arcryalis.gwentest.network.impl

import android.content.Context
import coil.Coil
import coil.ImageLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CoilImageCacherTest {

    private lateinit var context: Context
    private lateinit var recordingInterceptor: RecordingInterceptor

    private lateinit var sut: CoilImageCacherImpl

    @Before
    fun setupSut() {
        context = RuntimeEnvironment.getApplication()

        recordingInterceptor = RecordingInterceptor()
        val imageLoader = ImageLoader.Builder(context)
            .components { add(recordingInterceptor) }
            .interceptorDispatcher(UnconfinedTestDispatcher())
            .memoryCache(null)
            .diskCache(null)
            .build()
        Coil.setImageLoader(imageLoader)

        sut = CoilImageCacherImpl(context)
    }

    @Test
    fun givenUrl_whenQueueImageCacheRequest_thenEnqueuesRequestToCoil() {
        val url = "https://example.com/image.png"
        sut.queueImageCacheRequest(url)

        assertEquals(listOf(url), recordingInterceptor.requestedUrls)
    }

    @Test
    fun givenUrls_whenQueueImageCacheRequests_thenEnqueuesMultipleRequestsToCoil() {
        val urls = listOf("url1", "url2", "url3")
        sut.queueImageCacheRequests(urls)

        assertEquals(urls, recordingInterceptor.requestedUrls)
    }
}
