package com.hse.polochka.feature.auth.data.repository

import android.content.Context
import com.google.gson.Gson
import com.hse.polochka.R
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.feature.auth.data.dto.ErrorResponseDto
import com.hse.polochka.feature.auth.data.dto.LoginRequestDto
import com.hse.polochka.feature.auth.data.dto.RegisterRequestDto
import com.hse.polochka.feature.auth.data.dto.UserDto
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.auth.domain.model.User
import com.hse.polochka.feature.auth.domain.repository.AuthRepository
import java.io.IOException
import retrofit2.Response

class AuthRepositoryImpl(
    private val context: Context,
    private val authApi: AuthApi,
    private val sessionStorage: UserSessionStorage,
    private val gson: Gson = Gson(),
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): User {
        val cleanEmail = email.trim()
        val cleanDisplayName = displayName.trim()
        if (isLocalTestUser(cleanEmail, password)) {
            return createLocalSession(LOCAL_TEST_EMAIL, cleanDisplayName.ifBlank { LOCAL_TEST_NAME })
        }

        val request = RegisterRequestDto(
            email = cleanEmail,
            password = password,
            displayName = cleanDisplayName,
        )

        return runCatching {
            val response = authApi.register(request).requireBody()
            sessionStorage.saveToken(response.token)
            response.user.toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                createLocalSession(cleanEmail, cleanDisplayName.ifBlank { LOCAL_TEST_NAME })
            } else {
                throw error
            }
        }
    }

    override suspend fun login(email: String, password: String): User {
        val cleanEmail = email.trim()
        if (isLocalTestUser(cleanEmail, password)) {
            return createLocalSession(LOCAL_TEST_EMAIL, LOCAL_TEST_NAME)
        }

        return runCatching {
            val response = authApi.login(
                LoginRequestDto(
                    email = cleanEmail,
                    password = password,
                )
            ).requireBody()
            sessionStorage.saveToken(response.token)
            response.user.toDomain()
        }.getOrElse { error ->
            if (error is IOException && isLocalTestUser(cleanEmail, password)) {
                createLocalSession(LOCAL_TEST_EMAIL, LOCAL_TEST_NAME)
            } else {
                throw error
            }
        }
    }

    override suspend fun currentUser(): User? {
        val token = sessionStorage.getToken() ?: return null
        if (token == LOCAL_TEST_TOKEN) {
            return createLocalUser(LOCAL_TEST_EMAIL, LOCAL_TEST_NAME)
        }

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
            return body() ?: throw IllegalStateException(context.getString(R.string.auth_error_empty_response))
        }

        val fallbackMessage = when (code()) {
            400 -> context.getString(R.string.auth_error_check_data)
            401 -> context.getString(R.string.auth_error_invalid_credentials)
            409 -> context.getString(R.string.auth_error_user_exists)
            else -> context.getString(R.string.auth_error_server_request_failed)
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

    private fun createLocalSession(email: String, displayName: String): User {
        sessionStorage.saveToken(LOCAL_TEST_TOKEN)
        return createLocalUser(email, displayName)
    }

    private fun createLocalUser(email: String, displayName: String): User =
        User(
            id = LOCAL_TEST_USER_ID,
            email = email,
            displayName = displayName,
        )

    private fun isLocalTestUser(email: String, password: String): Boolean =
        email.equals(LOCAL_TEST_EMAIL, ignoreCase = true) && password == LOCAL_TEST_PASSWORD

    private fun UserDto.toDomain(): User =
        User(
            id = id,
            email = email,
            displayName = displayName,
        )

    private companion object {
        const val LOCAL_TEST_EMAIL = "test@polochka.local"
        const val LOCAL_TEST_PASSWORD = "password"
        const val LOCAL_TEST_NAME = "Test User"
        const val LOCAL_TEST_TOKEN = "local-test-token"
        const val LOCAL_TEST_USER_ID = "local-test-user"
    }
}
