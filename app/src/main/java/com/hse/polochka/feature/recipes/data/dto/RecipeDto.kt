package com.hse.polochka.feature.recipes.data.dto

data class RecipeDto(
    val id: String,
    val title: String,
    val description: String?,
    val time: String?,
    val category: String?,
    val tagIds: List<String>,
    val isFavorite: Boolean = false,
)
