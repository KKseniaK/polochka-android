package com.hse.polochka.feature.shopping.data.repository

import com.google.gson.Gson
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.shopping.data.dto.CreateShoppingItemRequestDto
import com.hse.polochka.feature.shopping.data.dto.DeleteShoppingItemRequestDto
import com.hse.polochka.feature.shopping.data.dto.MoveShoppingToStorageRequestDto
import com.hse.polochka.feature.shopping.data.dto.ShoppingConflictDto
import com.hse.polochka.feature.shopping.data.dto.ShoppingHistoryDto
import com.hse.polochka.feature.shopping.data.dto.ShoppingItemDto
import com.hse.polochka.feature.shopping.data.dto.UpdateShoppingItemRequestDto
import com.hse.polochka.feature.shopping.data.remote.ShoppingApi
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingActionState
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi
import retrofit2.Response

class ShoppingRepositoryImpl(
    private val shoppingApi: ShoppingApi,
    private val authApi: AuthApi,
    private val authHeaderProvider: AuthHeaderProvider,
    private val gson: Gson = Gson(),
) {

    suspend fun currentUserId(): String =
        authApi.me(authHeaderProvider.bearer()).requireBody().id

    suspend fun getItems(): List<ShoppingItemUi> =
        shoppingApi.getItems(authHeaderProvider.bearer())
            .requireShoppingBody()
            .map { it.toUi() }

    suspend fun addItem(title: String): ShoppingItemUi =
        shoppingApi.addItem(
            authorization = authHeaderProvider.bearer(),
            request = CreateShoppingItemRequestDto(title = title),
        ).requireShoppingBody().toUi()

    suspend fun updateItem(item: ShoppingItemUi, isChecked: Boolean): ShoppingItemUi =
        shoppingApi.updateItem(
            authorization = authHeaderProvider.bearer(),
            itemId = item.id,
            request = UpdateShoppingItemRequestDto(
                isChecked = isChecked,
                version = item.version,
            ),
        ).requireShoppingBody().toUi()

    suspend fun deleteItem(item: ShoppingItemUi, reason: String): ShoppingItemUi =
        shoppingApi.deleteItem(
            authorization = authHeaderProvider.bearer(),
            itemId = item.id,
            request = DeleteShoppingItemRequestDto(
                reason = reason,
                version = item.version,
            ),
        ).requireShoppingBody().toUi()

    suspend fun moveToStorage(items: List<ShoppingItemUi>) {
        shoppingApi.moveToStorage(
            authorization = authHeaderProvider.bearer(),
            request = MoveShoppingToStorageRequestDto(itemIds = items.map { it.id }),
        ).requireShoppingBody()
    }

    suspend fun getHistory(): List<ShoppingHistoryUi> =
        shoppingApi.getHistory(authHeaderProvider.bearer())
            .requireShoppingBody()
            .map { it.toUi() }

    private fun ShoppingItemDto.toUi(): ShoppingItemUi =
        ShoppingItemUi(
            id = id,
            title = title,
            isChecked = isChecked,
            version = version,
            createdByUserId = createdByUserId,
            createdByUserName = createdByUserName,
            familyActionState = familyActionState.toActionState(),
        )

    private fun ShoppingHistoryDto.toUi(): ShoppingHistoryUi =
        ShoppingHistoryUi(
            id = id,
            date = date,
            items = items,
        )

    private fun String.toActionState(): FamilyShoppingActionState =
        when (this) {
            "deleted_by_owner" -> FamilyShoppingActionState.ALREADY_DELETED_BY_OWNER
            "bought_by_owner" -> FamilyShoppingActionState.ALREADY_BOUGHT_BY_OWNER
            "bought_by_you" -> FamilyShoppingActionState.ALREADY_BOUGHT_BY_YOU
            else -> FamilyShoppingActionState.AVAILABLE
        }

    private fun <T> Response<T>.requireShoppingBody(): T {
        if (isSuccessful) {
            return body() ?: throw IllegalStateException("Empty server response")
        }

        if (code() == 409) {
            val conflict = errorBody()?.string()?.let { rawBody ->
                runCatching { gson.fromJson(rawBody, ShoppingConflictDto::class.java) }.getOrNull()
            }
            throw ShoppingConflictException(conflict?.code.orEmpty(), conflict?.item?.toUi())
        }

        throw IllegalStateException("Server request failed: HTTP ${code()}")
    }
}

class ShoppingConflictException(
    val code: String,
    val item: ShoppingItemUi?,
) : IllegalStateException(code)
