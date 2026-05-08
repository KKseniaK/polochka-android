package com.hse.polochka.feature.auth.data.remote

import com.hse.polochka.feature.auth.data.dto.AuthResponseDto
import com.hse.polochka.feature.auth.data.dto.LoginRequestDto
import com.hse.polochka.feature.auth.data.dto.RegisterRequestDto
import com.hse.polochka.feature.auth.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): Response<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): Response<AuthResponseDto>

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") authorization: String,
    ): Response<UserDto>
}
