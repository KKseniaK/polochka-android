package com.hse.polochka.feature.storage.domain.repository

import com.hse.polochka.core.storage_events.StorageEvent
import com.hse.polochka.feature.storage.data.dto.CatalogSuggestionDto
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi

interface StorageRepository {
    suspend fun getProducts(): List<StorageProductUi>
    suspend fun searchCatalog(query: String): List<CatalogSuggestionDto>
    suspend fun addProduct(
        name: String,
        amount: String,
        tagIds: List<String>,
        expirationAtMillis: Long?,
    ): StorageProductUi
    suspend fun writeOffProduct(productId: Int, reason: String): StorageEvent
}
