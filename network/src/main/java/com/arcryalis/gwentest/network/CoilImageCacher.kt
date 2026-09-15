package com.arcryalis.gwentest.network

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilImageCacher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun queueImageCacheRequests(urls: List<String>) {
        urls.forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            context.imageLoader.enqueue(request)
        }
    }
}