package com.hse.polochka.feature.analytics.data.dto

data class AnalyticsSummaryDto(
    val month: String,
    val boughtProductsCount: Int,
    val savedMoneyRub: Int,
    val percentChart: List<AnalyticsMetricDto>,
    val countChart: List<AnalyticsMetricDto>,
    val insights: AnalyticsInsightsDto,
)

data class AnalyticsMetricDto(
    val key: String,
    val label: String,
    val value: Int,
)

data class AnalyticsInsightsDto(
    val oftenBought: AnalyticsInsightDto,
    val favoriteCategory: AnalyticsInsightDto,
    val familyFavoriteCategory: AnalyticsInsightDto,
    val oftenSpoiled: AnalyticsInsightDto,
    val purchaseChampion: AnalyticsChampionDto,
)

data class AnalyticsInsightDto(
    val title: String,
    val description: String,
    val iconKey: String,
)

data class AnalyticsChampionDto(
    val userId: String,
    val displayName: String,
    val purchasesCount: Int,
)
