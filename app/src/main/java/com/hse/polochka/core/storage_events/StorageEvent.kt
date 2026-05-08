package com.hse.polochka.core.storage_events

data class StorageEvent(
    val productId: Int,
    val eventType: String,
    val happenedAtMillis: Long,
    val reason: String,
)
