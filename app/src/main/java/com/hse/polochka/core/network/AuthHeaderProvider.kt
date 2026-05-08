package com.hse.polochka.core.network

import com.hse.polochka.core.storage.UserSessionStorage

class AuthHeaderProvider(
    private val sessionStorage: UserSessionStorage,
) {
    fun bearer(): String = "Bearer ${sessionStorage.getToken().orEmpty()}"
}
