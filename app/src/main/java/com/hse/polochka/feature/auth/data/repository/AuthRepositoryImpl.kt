package com.hse.polochka.feature.auth.data.repository

import com.google.gson.Gson
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.feature.auth.data.dto.ErrorResponseDto
import com.hse.polochka.feature.auth.data.dto.LoginRequestDto
import com.hse.polochka.feature.auth.data.dto.RegisterRequestDto
import com.hse.polochka.feature.auth.data.dto.UserDto
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.auth.domain.model.User
import com.hse.polochka.feature.auth.domain.repository.AuthRepository
import retrofit2.Response

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionStorage: UserSessionStorage,
    private val gson: Gson = Gson(),
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): User {
        val response = authApi.register(
            RegisterRequestDto(
                email = email.trim(),
                password = password,
                displayName = displayName.trim(),
            )
        ).requireBody()
        sessionStorage.saveToken(response.token)
        return response.user.toDomain()
    }

    override suspend fun login(email: String, password: String): User {
        val response = authApi.login(
            LoginRequestDto(
                email = email.trim(),
                password = password,
            )
        ).requireBody()
        sessionStorage.saveToken(response.token)
        return response.user.toDomain()
    }

    override suspend fun currentUser(): User? {
        val token = sessionStorage.getToken() ?: return null
        return runCatching {
            authApi.me("Bearer $token").requireBody().toDomain()
        }.onFailure {
            sessionStorage.clear()
        }.getOrNull()
    }

    override fun hasToken(): Boolean = sessionStorage.getToken() != null

    override fun logout() {
        sessionStorage.clear()
    }

    private fun <T> Response<T>.requireBody(): T {
        if (isSuccessful) {
            return body() ?: throw IllegalStateException("Empty server response")
        }

        val fallbackMessage = when (code()) {
            400 -> "Check the entered data"
            401 -> "Invalid email or password"
            409 -> "User already exists"
            else -> "Server request failed"
        }
        val message = errorBody()?.string()?.let(::parseErrorMessage)
        throw IllegalStateException(message ?: fallbackMessage)
    }

    private fun parseErrorMessage(rawBody: String): String? {
        if (rawBody.isBlank()) return null
        return runCatching {
            gson.fromJson(rawBody, ErrorResponseDto::class.java).message
        }.getOrNull()
    }

    private fun UserDto.toDomain(): User =
        User(
            id = id,
            email = email,
            displayName = displayName,
        )
}
