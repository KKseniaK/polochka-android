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
)
