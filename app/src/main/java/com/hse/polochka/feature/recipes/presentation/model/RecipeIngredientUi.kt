package com.hse.polochka.feature.recipes.presentation.model

data class RecipeIngredientUi(
    val id: Int,
    val name: String,
    val amountForOnePortion: String,
    val isAvailable: Boolean,
    val kind: String = "product",
    val isRequiredForAvailability: Boolean = true,
)
