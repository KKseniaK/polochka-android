package com.hse.polochka.feature.recipes.domain.repository

import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

interface RecipesRepository {
    suspend fun getRecipes(preferences: TagPreferenceState): List<RecipeUi>
}
