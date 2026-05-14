package com.hse.polochka.feature.shopping.data.dto

data class ShoppingItemDto(
    val id: Int,
    val title: String,
    val isChecked: Boolean,
    val version: Int,
    val createdByUserId: String,
    val createdByUserName: String,
    val familyActionState: String,
)

data class CreateShoppingItemRequestDto(
    val title: String,
)

data class UpdateShoppingItemRequestDto(
    val isChecked: Boolean,
    val version: Int,
)

data class DeleteShoppingItemRequestDto(
    val reason: String,
    val version: Int,
)

data class MoveShoppingToStorageRequestDto(
    val itemIds: List<Int>,
)

data class ShoppingHistoryDto(
    val id: Int,
    val date: String,
    val items: List<String>,
)

data class ShoppingConflictDto(
    val code: String,
    val message: String?,
    val item: ShoppingItemDto?,
)
