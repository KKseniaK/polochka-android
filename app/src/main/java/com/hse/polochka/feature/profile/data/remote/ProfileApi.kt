package com.hse.polochka.feature.profile.data.remote

import com.hse.polochka.feature.profile.data.dto.ProfileDto
import com.hse.polochka.feature.profile.data.dto.UpdateProfileRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface ProfileApi {

    @GET("me/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String,
    ): Response<ProfileDto>

    @PUT("me/profile")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfileRequestDto,
    ): Response<ProfileDto>
}
