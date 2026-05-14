package com.hse.polochka.feature.shopping.presentation.model

data class ShoppingItemUi(
    val id: Int,
    val title: String,
    val isChecked: Boolean = false,
    val version: Int = 1,
    val createdByUserId: String = "",
    val createdByUserName: String = "",
    val familyActionState: FamilyShoppingActionState = FamilyShoppingActionState.AVAILABLE,
)

enum class FamilyShoppingActionState {
    AVAILABLE,
    ALREADY_DELETED_BY_OWNER,
    ALREADY_BOUGHT_BY_OWNER,
    ALREADY_BOUGHT_BY_YOU,
}
