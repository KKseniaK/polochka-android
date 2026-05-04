package com.hse.polochka.feature.recipes.presentation.model

data class RecipeStepUi(
    val id: Int,
    val stepNumber: Int,
    val text: String,
    val isExpanded: Boolean = false
)