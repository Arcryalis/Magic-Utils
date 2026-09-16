package com.arcryalis.gwentest.network

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallHeaderInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    companion object {
        const val LOG_TAG = "ScryfallInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = try {
            chain
                .request()
                .newBuilder()
                .addHeader("User-Agent", "com.arcryalis.gwentest/1.0")
                .addHeader("Accept", "application/json")
                .build()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error adding header: ${e.message}")
            chain.request()
        }

        return chain.proceed(request)
    }
}