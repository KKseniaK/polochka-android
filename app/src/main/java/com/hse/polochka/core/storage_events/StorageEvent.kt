package com.hse.polochka.core.storage_events

data class StorageEvent(
    val productId: Int,
    val eventType: String,
    val happenedAtMillis: Long,
    val reason: String,
    val userId: String? = "local-test-user",
    val userName: String? = "Вы",
    val productName: String? = "",
    val category: String? = "Другое",
    val quantity: Int = 1,
    val estimatedPriceRub: Int = 0,
)
