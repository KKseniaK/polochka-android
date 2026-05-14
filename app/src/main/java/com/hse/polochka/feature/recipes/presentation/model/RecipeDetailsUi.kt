package com.hse.polochka.feature.recipes.presentation.model

data class RecipeDetailsUi(
    val id: Int,
    val title: String,
    val time: String,
    val imageResId: Int?,
    val tagIds: List<String>,
    val ingredients: List<RecipeIngredientUi>,
    val steps: List<RecipeStepUi>,
    val missingCount: Int,
    val isFavorite: Boolean,
)
