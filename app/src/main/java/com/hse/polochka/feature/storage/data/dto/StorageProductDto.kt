package com.hse.polochka.feature.storage.data.dto

data class StorageProductDto(
    val id: Int,
    val name: String,
    val amount: String,
    val tagIds: List<String>,
    val imageKey: String?,
    val addedAtMillis: Long,
    val expirationAtMillis: Long?,
    val isWrittenOff: Boolean,
)

data class CreateStorageProductRequestDto(
    val name: String,
    val amount: String,
    val tagIds: List<String>,
    val expirationAtMillis: Long?,
)

data class WriteOffStorageProductRequestDto(
    val reason: String,
)

data class StorageEventDto(
    val productId: Int,
    val eventType: String,
    val happenedAtMillis: Long,
    val reason: String,
    val userId: String? = null,
    val userName: String? = null,
    val productName: String? = null,
    val category: String? = null,
    val quantity: Int = 1,
    val estimatedPriceRub: Int = 0,
)

data class CatalogSuggestionDto(
    val id: String,
    val name: String,
    val tagIds: List<String>,
    val imageKey: String?,
    val source: String,
)
