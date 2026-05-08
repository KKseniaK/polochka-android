package com.hse.polochka.feature.analytics.presentation.state

import com.hse.polochka.feature.analytics.domain.model.AnalyticsSummary

sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data class Content(val summary: AnalyticsSummary) : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
}
