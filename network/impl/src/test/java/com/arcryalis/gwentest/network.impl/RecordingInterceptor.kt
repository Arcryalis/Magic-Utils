package com.arcryalis.gwentest.network.impl

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil.decode.DataSource
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.request.SuccessResult

class RecordingInterceptor : Interceptor {

    val requestedUrls = mutableListOf<String>()

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        requestedUrls.add(chain.request.data as String)
        return SuccessResult(
            drawable = ColorDrawable(Color.TRANSPARENT),
            request = chain.request,
            dataSource = DataSource.MEMORY
        )
    }
}