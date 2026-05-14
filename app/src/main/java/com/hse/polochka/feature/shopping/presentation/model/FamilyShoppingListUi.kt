package com.hse.polochka.feature.shopping.presentation.model

data class FamilyShoppingListUi(
    val id: Int,
    val ownerUserId: String = "",
    val ownerName: String,
    val items: List<ShoppingItemUi>
)
