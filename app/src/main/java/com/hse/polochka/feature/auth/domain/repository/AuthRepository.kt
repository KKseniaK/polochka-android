package com.hse.polochka.feature.auth.domain.repository

import com.hse.polochka.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, displayName: String): User
    suspend fun login(email: String, password: String): User
    suspend fun currentUser(): User?
    fun hasToken(): Boolean
    fun logout()
}
