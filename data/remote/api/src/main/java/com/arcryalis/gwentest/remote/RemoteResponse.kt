package com.arcryalis.gwentest.remote

sealed interface RemoteResponse<T> {
    data class Success<T>(val data: T) : RemoteResponse<T>
    data class Error<T>(val message: String? = null) : RemoteResponse<T>
}