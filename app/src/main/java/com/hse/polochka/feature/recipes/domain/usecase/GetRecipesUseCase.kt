package com.hse.polochka.feature.recipes.domain.usecase

import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.recipes.domain.repository.RecipesRepository
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class GetRecipesUseCase(
    private val repository: RecipesRepository,
) {
    suspend operator fun invoke(preferences: TagPreferenceState): List<RecipeUi> =
        repository.getRecipes(preferences)
}
