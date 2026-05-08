package com.hse.polochka.feature.onboarding.data.remote

import com.hse.polochka.feature.onboarding.data.dto.CompleteOnboardingRequestDto
import com.hse.polochka.feature.onboarding.data.dto.OnboardingStateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface OnboardingApi {

    @GET("me/onboarding")
    suspend fun getOnboardingState(
        @Header("Authorization") authorization: String,
    ): Response<OnboardingStateDto>

    @PUT("me/onboarding")
    suspend fun completeOnboarding(
        @Header("Authorization") authorization: String,
        @Body request: CompleteOnboardingRequestDto,
    ): Response<OnboardingStateDto>
}
