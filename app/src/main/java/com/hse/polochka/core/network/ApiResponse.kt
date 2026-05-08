package com.hse.polochka.core.network

import retrofit2.Response

fun <T> Response<T>.requireBody(): T {
    if (isSuccessful) {
        return body() ?: throw IllegalStateException("Empty server response")
    }
    throw IllegalStateException("Server request failed: HTTP ${code()}")
}
