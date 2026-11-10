package com.arcryalis.gwentest.remote

sealed interface RemoteResponse<out T> {
    data class Success<out T>(val data: T) : RemoteResponse<T>
    data class Error(val message: String? = null) : RemoteResponse<Nothing>
}
