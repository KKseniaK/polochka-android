package com.hse.polochka.feature.recipes.data

import android.content.Context
import com.hse.polochka.R
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.feature.recipes.data.dto.RecipeDto
import com.hse.polochka.feature.recipes.data.dto.RecipeIngredientDto
import com.hse.polochka.feature.recipes.data.dto.RecipeStepDto
import com.hse.polochka.feature.recipes.data.remote.RecipesApi
import com.hse.polochka.feature.recipes.domain.repository.RecipesRepository
import com.hse.polochka.feature.recipes.presentation.model.RecipeDetailsUi
import com.hse.polochka.feature.recipes.presentation.model.RecipeIngredientUi
import com.hse.polochka.feature.recipes.presentation.model.RecipeStepUi
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class RecipeRepository(
    private val context: Context,
    private val recipesApi: RecipesApi = ApiClient.create(RecipesApi::class.java),
    private val authHeaderProvider: AuthHeaderProvider = AuthHeaderProvider(UserSessionStorage(context)),
) : RecipesRepository {

    fun getFilterTags(): List<String> =
        listOf("poultry", "cheese", "vegetables", "pasta", "seafood", "meat", "fish", "grains", "sweet")

    override suspend fun getRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(
            recipesApi.getRecipes(authHeaderProvider.bearer()).requireBody().map { it.toUi() },
            preferences,
        )

    suspend fun getRecipeDetails(recipeId: Int): RecipeDetailsUi =
        recipesApi.getRecipe(authHeaderProvider.bearer(), recipeId)
            .requireBody()
            .toDetailsUi()

    suspend fun addMissingToShopping(recipeId: Int): Int =
        recipesApi.addMissingToShopping(authHeaderProvider.bearer(), recipeId)
            .requireBody()
            .addedCount

    private fun personalize(
        recipes: List<RecipeUi>,
        preferences: TagPreferenceState,
    ): List<RecipeUi> {
        val likedTagIds = preferences.likedTagIds.toSet()
        val restrictedTagIds = preferences.restrictedTagIds.toSet()

        return recipes.map { recipe ->
            val matchedLikedTags = recipe.tagIds.count { it in likedTagIds }
            val hasConflict = recipe.hasPreferenceConflict || recipe.tagIds.any { it in restrictedTagIds }
            recipe.copy(
                preferenceScore = matchedLikedTags,
                hasPreferenceConflict = hasConflict,
                personalizedStatus = when {
                    hasConflict -> context.getString(R.string.recipes_preference_conflict)
                    matchedLikedTags > 0 -> context.getString(R.string.recipes_preference_match)
                    else -> null
                },
            )
        }
    }

    private fun RecipeDto.toUi(): RecipeUi {
        val placeholder = RecipePlaceholder(R.drawable.ic_recipe_salad, R.color.recipe_placeholder_background)
        return RecipeUi(
            id = id.toIntOrNull() ?: id.hashCode(),
            title = title,
            status = status ?: if (canCook) "все ингредиенты в наличии" else "проверьте ингредиенты",
            time = time.orEmpty().ifBlank { "30 мин" },
            category = category.orEmpty().ifBlank { "рецепт" },
            imageResId = imageKey.toImageResId(),
            placeholderIconResId = placeholder.iconResId,
            placeholderColorResId = placeholder.colorResId,
            canCook = canCook,
            tagIds = tagIds,
            isFavorite = isFavorite,
            missingCount = missingCount,
            hasStorageProducts = hasStorageProducts,
            hasPreferenceConflict = hasPreferenceConflict,
        )
    }

    private fun RecipeDto.toDetailsUi(): RecipeDetailsUi =
        RecipeDetailsUi(
            id = id.toIntOrNull() ?: id.hashCode(),
            title = title,
            time = time.orEmpty().ifBlank { "30 мин" },
            imageResId = imageKey.toImageResId(),
            tagIds = tagIds,
            ingredients = ingredients.map { it.toUi() },
            steps = steps.map { it.toUi() },
            missingCount = missingCount,
            isFavorite = isFavorite,
        )

    private fun RecipeIngredientDto.toUi(): RecipeIngredientUi =
        RecipeIngredientUi(
            id = id.hashCode(),
            name = name,
            amountForOnePortion = amount,
            isAvailable = isAvailable,
            kind = kind,
            isRequiredForAvailability = isRequiredForAvailability,
        )

    private fun RecipeStepDto.toUi(): RecipeStepUi =
        RecipeStepUi(
            id = id.hashCode(),
            stepNumber = stepNumber,
            text = text,
            isExpanded = stepNumber == 1,
        )

    private fun String?.toImageResId(): Int? =
        this
            ?.takeIf { it.isNotBlank() }
            ?.let { imageKey ->
                context.resources.getIdentifier(imageKey, "drawable", context.packageName)
                    .takeIf { it != 0 }
            }

    private data class RecipePlaceholder(
        val iconResId: Int,
        val colorResId: Int,
    )
}
