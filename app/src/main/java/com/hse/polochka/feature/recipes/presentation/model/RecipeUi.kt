package com.hse.polochka.feature.recipes.presentation.model

data class RecipeUi(
    val id: Int,
    val title: String,
    val status: String,
    val time: String,
    val category: String,
    val imageResId: Int,
    val isFavorite: Boolean = false
)