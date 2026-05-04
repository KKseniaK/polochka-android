package com.hse.polochka.feature.shopping.presentation.model

data class FamilyShoppingListUi(
    val id: Int,
    val ownerName: String,
    val items: List<ShoppingItemUi>
)