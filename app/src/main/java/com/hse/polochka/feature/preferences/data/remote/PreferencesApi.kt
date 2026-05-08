package com.hse.polochka.feature.preferences.data.remote

import com.hse.polochka.feature.preferences.data.dto.PreferencesDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface PreferencesApi {

    @GET("me/preferences")
    suspend fun getPreferences(): Response<PreferencesDto>

    @PUT("me/preferences")
    suspend fun updatePreferences(@Body preferences: PreferencesDto): Response<PreferencesDto>
}
