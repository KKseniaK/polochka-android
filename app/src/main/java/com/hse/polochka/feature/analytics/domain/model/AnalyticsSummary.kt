package com.hse.polochka.feature.analytics.domain.model

data class AnalyticsSummary(
    val month: String,
    val boughtProductsCount: Int,
    val savedMoneyRub: Int,
    val percentChart: List<AnalyticsMetric>,
    val countChart: List<AnalyticsMetric>,
    val insights: AnalyticsInsights,
)

data class AnalyticsMetric(
    val key: String,
    val label: String,
    val value: Int,
)

data class AnalyticsInsights(
    val oftenBought: AnalyticsInsight,
    val favoriteCategory: AnalyticsInsight,
    val familyFavoriteCategory: AnalyticsInsight,
    val oftenSpoiled: AnalyticsInsight,
    val purchaseChampion: AnalyticsChampion,
)

data class AnalyticsInsight(
    val title: String,
    val description: String,
    val iconKey: String,
)

data class AnalyticsChampion(
    val userId: String,
    val displayName: String,
    val purchasesCount: Int,
)
