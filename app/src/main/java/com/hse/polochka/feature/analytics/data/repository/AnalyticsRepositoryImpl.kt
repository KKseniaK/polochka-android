package com.hse.polochka.feature.analytics.data.repository

import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.storage_events.StorageEvent
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.feature.analytics.data.dto.AnalyticsChampionDto
import com.hse.polochka.feature.analytics.data.dto.AnalyticsInsightDto
import com.hse.polochka.feature.analytics.data.dto.AnalyticsInsightsDto
import com.hse.polochka.feature.analytics.data.dto.AnalyticsMetricDto
import com.hse.polochka.feature.analytics.data.dto.AnalyticsSummaryDto
import com.hse.polochka.feature.analytics.data.remote.AnalyticsApi
import com.hse.polochka.feature.analytics.domain.model.AnalyticsChampion
import com.hse.polochka.feature.analytics.domain.model.AnalyticsInsight
import com.hse.polochka.feature.analytics.domain.model.AnalyticsInsights
import com.hse.polochka.feature.analytics.domain.model.AnalyticsMetric
import com.hse.polochka.feature.analytics.domain.model.AnalyticsSummary
import com.hse.polochka.feature.analytics.domain.repository.AnalyticsRepository
import java.io.IOException
import java.util.Calendar

class AnalyticsRepositoryImpl(
    private val analyticsApi: AnalyticsApi,
    private val eventStorage: StorageEventStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : AnalyticsRepository {

    override suspend fun getMonthlySummary(month: String): AnalyticsSummary =
        runCatching {
            analyticsApi.getMonthlySummary(
                authorization = authHeaderProvider.bearer(),
                month = month,
            ).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                buildLocalSummary(month)
            } else {
                throw error
            }
        }

    private fun buildLocalSummary(month: String): AnalyticsSummary {
        val events = eventStorage.getEvents()
            .filter { it.isInMonth(month) }
        val boughtEvents = events.filter { it.eventType in BOUGHT_EVENT_TYPES }
        val usedEvents = events.filter { it.eventType == EVENT_USED }
        val spoiledEvents = usedEvents.filter { it.reason == REASON_SPOILED }
        val savedEvents = usedEvents.filter { it.reason != REASON_SPOILED }

        return AnalyticsSummary(
            month = month,
            boughtProductsCount = boughtEvents.sumOf { it.quantity.safeQuantity() },
            savedMoneyRub = savedEvents.sumOf { it.safePrice() },
            percentChart = listOf(
                AnalyticsMetric("saved", "Спасено", usedEvents.countSaved()),
                AnalyticsMetric("spoiled", "Испортилось", usedEvents.countByReason(REASON_SPOILED)),
                AnalyticsMetric("eaten", "Съели", usedEvents.countByReason(REASON_EATEN)),
            ),
            countChart = listOf(
                AnalyticsMetric("spoiled", "Испортилось", usedEvents.countByReason(REASON_SPOILED)),
                AnalyticsMetric("eaten", "Съели", usedEvents.countByReason(REASON_EATEN)),
                AnalyticsMetric("thrownAway", "Выкинули", usedEvents.countThrownAway()),
            ),
            insights = AnalyticsInsights(
                oftenBought = AnalyticsInsight(
                    title = boughtEvents.topProductName().ifBlank { "Молоко" },
                    description = "чаще всего\nпокупали",
                    iconKey = boughtEvents.topIconKey(),
                ),
                favoriteCategory = AnalyticsInsight(
                    title = events.topCategory().ifBlank { "Острое" },
                    description = "ваша любимая\nкатегория",
                    iconKey = events.topCategory().toIconKey(),
                ),
                familyFavoriteCategory = AnalyticsInsight(
                    title = events.filter { it.userId != LOCAL_USER_ID }.topCategory().ifBlank { "Сыры" },
                    description = "любимая\nкатегория семьи",
                    iconKey = events.filter { it.userId != LOCAL_USER_ID }.topCategory().toIconKey(),
                ),
                oftenSpoiled = AnalyticsInsight(
                    title = spoiledEvents.topProductName().ifBlank { "Молоко" },
                    description = "чаще всего\nпортилось",
                    iconKey = spoiledEvents.topIconKey(),
                ),
                purchaseChampion = boughtEvents.purchaseChampion(),
            ),
        )
    }

    private fun AnalyticsSummaryDto.toDomain(): AnalyticsSummary =
        AnalyticsSummary(
            month = month,
            boughtProductsCount = boughtProductsCount,
            savedMoneyRub = savedMoneyRub,
            percentChart = percentChart.map { it.toDomain() },
            countChart = countChart.map { it.toDomain() },
            insights = insights.toDomain(),
        )

    private fun AnalyticsMetricDto.toDomain(): AnalyticsMetric =
        AnalyticsMetric(key = key, label = label, value = value)

    private fun AnalyticsInsightsDto.toDomain(): AnalyticsInsights =
        AnalyticsInsights(
            oftenBought = oftenBought.toDomain(),
            favoriteCategory = favoriteCategory.toDomain(),
            familyFavoriteCategory = familyFavoriteCategory.toDomain(),
            oftenSpoiled = oftenSpoiled.toDomain(),
            purchaseChampion = purchaseChampion.toDomain(),
        )

    private fun AnalyticsInsightDto.toDomain(): AnalyticsInsight =
        AnalyticsInsight(title = title, description = description, iconKey = iconKey)

    private fun AnalyticsChampionDto.toDomain(): AnalyticsChampion =
        AnalyticsChampion(userId = userId, displayName = displayName, purchasesCount = purchasesCount)

    private fun StorageEvent.isInMonth(month: String): Boolean {
        val parts = month.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: return false
        val monthIndex = (parts.getOrNull(1)?.toIntOrNull() ?: return false) - 1
        val calendar = Calendar.getInstance().apply {
            timeInMillis = happenedAtMillis
        }
        return calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == monthIndex
    }

    private fun List<StorageEvent>.countByReason(reason: String): Int =
        filter { it.reason == reason }.sumOf { it.quantity.safeQuantity() }

    private fun List<StorageEvent>.countSaved(): Int =
        filter { it.reason.startsWith(REASON_CUSTOM_PREFIX) }.sumOf { it.quantity.safeQuantity() }

    private fun List<StorageEvent>.countThrownAway(): Int =
        filter { event ->
            event.reason.startsWith(REASON_CUSTOM_PREFIX) ||
                event.reason == REASON_THROWN_AWAY ||
                event.reason == REASON_NOT_NEEDED
        }.sumOf { it.quantity.safeQuantity() }

    private fun List<StorageEvent>.topProductName(): String =
        groupBy { it.productName.orEmpty() }
            .filterKeys { it.isNotBlank() }
            .maxByOrNull { (_, events) -> events.sumOf { it.quantity.safeQuantity() } }
            ?.key
            .orEmpty()

    private fun List<StorageEvent>.topCategory(): String =
        groupBy { it.safeCategory() }
            .maxByOrNull { (_, events) -> events.sumOf { it.quantity.safeQuantity() } }
            ?.key
            .orEmpty()

    private fun List<StorageEvent>.topIconKey(): String = firstOrNull()?.safeCategory().orEmpty().toIconKey()

    private fun List<StorageEvent>.purchaseChampion(): AnalyticsChampion {
        val champion = groupBy { it.userId.orEmpty() }
            .maxByOrNull { (_, events) -> events.sumOf { it.quantity.safeQuantity() } }

        val events = champion?.value.orEmpty()
        return AnalyticsChampion(
            userId = champion?.key.orEmpty().ifBlank { LOCAL_USER_ID },
            displayName = events.firstOrNull()?.userName.orEmpty().ifBlank { "Вы" },
            purchasesCount = events.sumOf { it.quantity.safeQuantity() },
        )
    }

    private fun StorageEvent.safeCategory(): String = category.orEmpty().ifBlank { "Другое" }

    private fun String.toIconKey(): String =
        when (lowercase()) {
            "молочка" -> "milk"
            "сыры" -> "cheese"
            "база" -> "grains"
            else -> "hot"
        }

    private fun StorageEvent.safePrice(): Int =
        estimatedPriceRub.takeIf { it > 0 } ?: DEFAULT_PRODUCT_PRICE_RUB * quantity.safeQuantity()

    private fun Int.safeQuantity(): Int = coerceAtLeast(1)

    private fun sampleEvents(): List<StorageEvent> {
        val now = System.currentTimeMillis()
        return listOf(
            StorageEvent(1, EVENT_BOUGHT, now - days(2), "shopping", "dad", "Папа", "Молоко", "Молочка", 3, 320),
            StorageEvent(2, EVENT_BOUGHT, now - days(3), "shopping", "mom", "Мама", "Сыр", "Сыры", 2, 460),
            StorageEvent(3, EVENT_BOUGHT, now - days(4), "shopping", LOCAL_USER_ID, "Вы", "Яйца", "База", 1, 160),
            StorageEvent(7, EVENT_BOUGHT, now - days(7), "shopping", "dad", "Папа", "Хлеб", "База", 4, 280),
            StorageEvent(8, EVENT_BOUGHT, now - days(9), "shopping", "mom", "Мама", "Помидоры", "Овощи/фрукты", 5, 420),
            StorageEvent(9, EVENT_BOUGHT, now - days(11), "shopping", LOCAL_USER_ID, "Вы", "Макароны", "База", 2, 180),
            StorageEvent(10, EVENT_BOUGHT, now - days(13), "shopping", "dad", "Папа", "Йогурт", "Молочка", 3, 285),
            StorageEvent(11, EVENT_BOUGHT, now - days(15), "shopping", "mom", "Мама", "Яблоки", "Овощи/фрукты", 6, 360),
            StorageEvent(12, EVENT_BOUGHT, now - days(18), "shopping", LOCAL_USER_ID, "Вы", "Крупа", "База", 2, 210),
            StorageEvent(13, EVENT_BOUGHT, now - days(20), "shopping", "dad", "Папа", "Молоко", "Молочка", 4, 440),
            StorageEvent(4, EVENT_USED, now - days(1), REASON_EATEN, LOCAL_USER_ID, "Вы", "Молоко", "Молочка", 2, 220),
            StorageEvent(5, EVENT_USED, now - days(5), REASON_SPOILED, "dad", "Папа", "Йогурт", "Молочка", 1, 95),
            StorageEvent(6, EVENT_USED, now - days(6), REASON_CUSTOM_PREFIX + "отдали", "mom", "Мама", "Сыр", "Сыры", 1, 230),
        )
    }

    private fun days(value: Long): Long = value * 86_400_000L

    private companion object {
        const val LOCAL_USER_ID = "local-test-user"
        const val EVENT_USED = "used"
        const val EVENT_BOUGHT = "bought"
        const val EVENT_ADDED = "added"
        val BOUGHT_EVENT_TYPES = setOf(EVENT_BOUGHT, EVENT_ADDED)
        const val REASON_EATEN = "eaten"
        const val REASON_SPOILED = "spoiled"
        const val REASON_CUSTOM_PREFIX = "custom:"
        const val REASON_THROWN_AWAY = "thrown_away"
        const val REASON_NOT_NEEDED = "not_needed"
        const val DEFAULT_PRODUCT_PRICE_RUB = 120
    }
}
