package com.arcryalis.gwentest.network

class MockCoilImageCacher : CoilImageCacher {
    private val _requestedUrls = mutableListOf<String>()
    val requestedUrls: List<String> get() = _requestedUrls

    override fun queueImageCacheRequest(url: String) {
        _requestedUrls.add(url)
    }

    override fun queueImageCacheRequests(urls: List<String>) {
        _requestedUrls.addAll(urls)
    }

    fun clear() {
        _requestedUrls.clear()
    }
}
