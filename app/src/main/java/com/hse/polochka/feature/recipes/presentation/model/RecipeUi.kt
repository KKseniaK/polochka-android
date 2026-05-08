package com.hse.polochka.feature.recipes.presentation.model

data class RecipeUi(
    val id: Int,
    val title: String,
    val status: String,
    val time: String,
    val category: String,
    val imageResId: Int,
    val tagIds: List<String> = emptyList(),
    val personalizedStatus: String? = null,
    val hasPreferenceConflict: Boolean = false,
    val preferenceScore: Int = 0,
    val isFavorite: Boolean = false
)
