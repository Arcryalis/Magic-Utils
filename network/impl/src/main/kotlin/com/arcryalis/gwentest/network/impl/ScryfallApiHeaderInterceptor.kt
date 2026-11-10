package com.arcryalis.gwentest.network.impl

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallApiHeaderInterceptor @Inject constructor(
    private val scryfallUserAgentProvider: ScryfallUserAgentProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("User-Agent", scryfallUserAgentProvider.scryfallUserAgent)
            .header("Accept", "application/json")
            .build()

        return chain.proceed(request)
    }
}
