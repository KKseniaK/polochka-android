package com.hse.polochka.feature.analytics.domain.repository

import com.hse.polochka.feature.analytics.domain.model.AnalyticsSummary

interface AnalyticsRepository {
    suspend fun getMonthlySummary(month: String): AnalyticsSummary
}
