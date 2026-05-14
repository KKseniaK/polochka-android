package com.hse.polochka.feature.recipes.presentation.model

data class RecipeUi(
    val id: Int,
    val title: String,
    val status: String,
    val time: String,
    val category: String,
    val imageResId: Int?,
    val placeholderIconResId: Int,
    val placeholderColorResId: Int,
    val canCook: Boolean,
    val tagIds: List<String> = emptyList(),
    val personalizedStatus: String? = null,
    val hasPreferenceConflict: Boolean = false,
    val preferenceScore: Int = 0,
    val isFavorite: Boolean = false,
    val missingCount: Int = 0,
    val hasStorageProducts: Boolean = false,
)
