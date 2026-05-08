package com.hse.polochka.feature.analytics.data.remote

import com.hse.polochka.feature.analytics.data.dto.AnalyticsSummaryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AnalyticsApi {

    @GET("analytics/summary")
    suspend fun getMonthlySummary(
        @Header("Authorization") authorization: String,
        @Query("month") month: String,
    ): Response<AnalyticsSummaryDto>
}
