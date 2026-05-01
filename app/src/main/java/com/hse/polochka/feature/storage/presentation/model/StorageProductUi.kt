package com.hse.polochka.feature.storage.presentation.model

enum class ProductStorageStatus {
    EXPIRED,
    LAST_DAY,
    MIDDLE,
    FRESH
}

data class StorageProductUi(
    val id: Int,
    val name: String,
    val amount: String,
    val daysLeftText: String,
    val tags: List<String>,
    val imageResId: Int,
    val status: ProductStorageStatus
)