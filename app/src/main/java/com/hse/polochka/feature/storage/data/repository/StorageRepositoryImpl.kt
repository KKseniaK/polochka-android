package com.hse.polochka.feature.storage.data.repository

import com.hse.polochka.R
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.storage_events.StorageEvent
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.feature.storage.data.dto.CreateStorageProductRequestDto
import com.hse.polochka.feature.storage.data.dto.StorageEventDto
import com.hse.polochka.feature.storage.data.dto.StorageProductDto
import com.hse.polochka.feature.storage.data.dto.WriteOffStorageProductRequestDto
import com.hse.polochka.feature.storage.data.remote.StorageApi
import com.hse.polochka.feature.storage.domain.repository.StorageRepository
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi

class StorageRepositoryImpl(
    private val storageApi: StorageApi,
    private val eventStorage: StorageEventStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : StorageRepository {

    override suspend fun getProducts(): List<StorageProductUi> =
        storageApi.getProducts(authHeaderProvider.bearer())
            .requireBody()
            .map { it.toUi() }

    override suspend fun addProduct(
        name: String,
        amount: String,
        tagIds: List<String>,
        expirationAtMillis: Long?,
    ): StorageProductUi =
        storageApi.addProduct(
            authorization = authHeaderProvider.bearer(),
            request = CreateStorageProductRequestDto(
                name = name,
                amount = amount,
                tagIds = tagIds,
                expirationAtMillis = expirationAtMillis,
            ),
        ).requireBody().toUi()

    override suspend fun writeOffProduct(productId: Int, reason: String): StorageEvent {
        val event = storageApi.writeOffProduct(
            authorization = authHeaderProvider.bearer(),
            productId = productId,
            request = WriteOffStorageProductRequestDto(reason = reason),
        ).requireBody().toDomain()
        eventStorage.addEvents(listOf(event))
        return event
    }

    private fun StorageProductDto.toUi(): StorageProductUi =
        StorageProductUi(
            id = id,
            name = name,
            amount = amount,
            tags = tagIds,
            imageResId = imageKey.toImageResId(),
            addedAtMillis = addedAtMillis,
            expirationAtMillis = expirationAtMillis,
            isWrittenOff = isWrittenOff,
        )

    private fun StorageEventDto.toDomain(): StorageEvent =
        StorageEvent(
            productId = productId,
            eventType = eventType,
            happenedAtMillis = happenedAtMillis,
            reason = reason,
            userId = userId,
            userName = userName,
            productName = productName,
            category = category,
            quantity = quantity,
            estimatedPriceRub = estimatedPriceRub,
        )

    private fun String?.toImageResId(): Int =
        when (this) {
            "cheese" -> R.drawable.ic_cheese
            "grains" -> R.drawable.ic_grains
            "milk" -> R.drawable.ic_milk
            else -> R.drawable.ic_milk
        }
}
