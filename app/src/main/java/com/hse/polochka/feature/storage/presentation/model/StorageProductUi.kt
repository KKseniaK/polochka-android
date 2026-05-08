package com.hse.polochka.feature.storage.presentation.model

enum class ProductStorageStatus {
    EXPIRED,
    LAST_DAY,
    MIDDLE,
    FRESH,
    LONG_LIFE
}

data class StorageProductUi(
    val id: Int,
    val name: String,
    val amount: String,
    val tags: List<String>,
    val imageResId: Int,
    val addedAtMillis: Long,
    val expirationAtMillis: Long?,
    val isWrittenOff: Boolean = false,
) {
    val status: ProductStorageStatus
        get() = when {
            expirationAtMillis == null -> ProductStorageStatus.LONG_LIFE
            daysLeft < 0 -> ProductStorageStatus.EXPIRED
            daysLeft <= 1 -> ProductStorageStatus.LAST_DAY
            daysLeft <= 4 -> ProductStorageStatus.MIDDLE
            else -> ProductStorageStatus.FRESH
        }

    val daysLeft: Long
        get() = expirationAtMillis?.let { millisToUtcDay(it) - currentUtcDay() } ?: Long.MAX_VALUE

    val daysLeftText: String
        get() = when {
            expirationAtMillis == null -> "долго хранится"
            daysLeft < 0 -> "испорчено"
            daysLeft == 0L -> "осталось: сегодня"
            daysLeft == 1L -> "осталось: 1дн"
            else -> "осталось: ${daysLeft}дн"
        }

    val lifeProgress: Float
        get() {
            val expiresAt = expirationAtMillis ?: return 0f
            val totalDays = (millisToUtcDay(expiresAt) - millisToUtcDay(addedAtMillis)).coerceAtLeast(1)
            val elapsedDays = (currentUtcDay() - millisToUtcDay(addedAtMillis)).coerceIn(0, totalDays)
            return elapsedDays.toFloat() / totalDays.toFloat()
        }

    companion object {
        private const val MILLIS_IN_DAY = 86_400_000L

        private fun currentUtcDay(): Long = System.currentTimeMillis() / MILLIS_IN_DAY

        private fun millisToUtcDay(millis: Long): Long = millis / MILLIS_IN_DAY
    }
}
