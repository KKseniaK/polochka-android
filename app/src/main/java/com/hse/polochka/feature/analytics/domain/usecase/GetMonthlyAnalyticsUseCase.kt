package com.hse.polochka.feature.analytics.domain.usecase

import com.hse.polochka.feature.analytics.domain.model.AnalyticsSummary
import com.hse.polochka.feature.analytics.domain.repository.AnalyticsRepository

class GetMonthlyAnalyticsUseCase(
    private val repository: AnalyticsRepository,
) {
    suspend operator fun invoke(month: String): AnalyticsSummary =
        repository.getMonthlySummary(month)
}
