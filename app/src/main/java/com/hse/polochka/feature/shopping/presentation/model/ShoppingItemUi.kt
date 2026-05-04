package com.hse.polochka.feature.shopping.presentation.model

data class ShoppingItemUi(
    val id: Int,
    val title: String,
    val isChecked: Boolean = false
)