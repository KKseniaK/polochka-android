package com.hse.polochka.feature.shopping.presentation.model

data class ShoppingHistoryUi(
    val id: Int,
    val date: String,
    val items: List<String>
)