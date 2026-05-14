package com.hse.polochka.feature.recipes.data.dto

data class RecipeDto(
    val id: String,
    val title: String,
    val description: String?,
    val time: String?,
    val category: String?,
    val imageKey: String? = null,
    val status: String? = null,
    val canCook: Boolean = false,
    val tagIds: List<String>,
    val ingredients: List<RecipeIngredientDto> = emptyList(),
    val steps: List<RecipeStepDto> = emptyList(),
    val isFavorite: Boolean = false,
    val missingCount: Int = 0,
    val hasStorageProducts: Boolean = false,
    val hasPreferenceConflict: Boolean = false,
)

data class RecipeIngredientDto(
    val id: String,
    val name: String,
    val amount: String,
    val kind: String = "product",
    val isAvailable: Boolean = false,
    val isRequiredForAvailability: Boolean = true,
)

data class RecipeStepDto(
    val id: String,
    val stepNumber: Int,
    val text: String,
)

data class MissingToShoppingDto(
    val addedCount: Int,
)
