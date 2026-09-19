package com.arcryalis.gwentest.network

interface CoilImageCacher {
    fun queueImageCacheRequest(url: String)

    fun queueImageCacheRequests(urls: List<String>)
}