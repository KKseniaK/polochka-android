package com.hse.polochka.feature.recipes.data

import android.content.Context
import com.hse.polochka.R
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.recipes.data.dto.RecipeDto
import com.hse.polochka.feature.recipes.data.remote.RecipesApi
import com.hse.polochka.feature.recipes.domain.repository.RecipesRepository
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi
import java.io.IOException

class RecipeRepository(
    private val context: Context,
    private val recipesApi: RecipesApi = ApiClient.create(RecipesApi::class.java),
) : RecipesRepository {

    fun getFilterTags(): List<String> =
        listOf("сытно", "быстро", "супы", "каши", "завтрак", "без молочки")

    fun getHomeRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes().filter { it.canCook }, preferences).take(3)

    fun getCanCookRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes().filter { it.canCook }, preferences)

    fun getPopularRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes(), preferences)

    override suspend fun getRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        runCatching {
            personalize(
                recipesApi.getRecipes().requireBody().map { it.toUi() },
                preferences,
            )
        }.getOrElse { error ->
            if (error is IOException) {
                personalize(allRecipes(), preferences)
            } else {
                throw error
            }
        }

    private fun personalize(
        recipes: List<RecipeUi>,
        preferences: TagPreferenceState,
    ): List<RecipeUi> {
        val likedTagIds = preferences.likedTagIds.toSet()
        val restrictedTagIds = preferences.restrictedTagIds.toSet()

        return recipes
            .map { recipe ->
                val matchedLikedTags = recipe.tagIds.count { it in likedTagIds }
                val hasConflict = recipe.tagIds.any { it in restrictedTagIds }
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
            .sortedWith(
                compareBy<RecipeUi> { it.hasPreferenceConflict }
                    .thenByDescending { it.preferenceScore }
                    .thenBy { it.id }
            )
    }

    private fun allRecipes(): List<RecipeUi> =
        listOf(
            recipe(
                id = 1,
                title = "Щи с зеленью",
                status = "все ингредиенты в наличии",
                time = "30 мин",
                category = "суп",
                imageResId = R.drawable.ic_vegetables,
                tagIds = listOf("vegetables", "greens"),
                canCook = true,
            ),
            recipe(
                id = 2,
                title = "Сырники",
                status = "все ингредиенты в наличии",
                time = "20 мин",
                category = "завтрак",
                imageResId = null,
                tagIds = listOf("cheese", "milk", "sweet"),
                canCook = true,
                isFavorite = true,
            ),
            recipe(
                id = 3,
                title = "Овощной салат",
                status = "все ингредиенты в наличии",
                time = "10 мин",
                category = "быстро",
                imageResId = R.drawable.ic_vegetables,
                tagIds = listOf("vegetables", "greens", "diet"),
                canCook = true,
            ),
            recipe(
                id = 4,
                title = "Куриная каша",
                status = "не хватает 1 ингредиента",
                time = "35 мин",
                category = "каша",
                imageResId = null,
                tagIds = listOf("poultry", "grains", "protein"),
                canCook = false,
            ),
            recipe(
                id = 5,
                title = "Рамен",
                status = "не хватает 2 ингредиентов",
                time = "40 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                tagIds = listOf("pasta", "meat", "hot"),
                canCook = false,
            ),
            recipe(
                id = 6,
                title = "Паста карбонара",
                status = "не хватает 3 ингредиентов",
                time = "15 мин",
                category = "сытно",
                imageResId = null,
                tagIds = listOf("pasta", "cheese", "meat"),
                canCook = false,
            ),
            recipe(
                id = 7,
                title = "Рыба с овощами",
                status = "не хватает 1 ингредиента",
                time = "25 мин",
                category = "ужин",
                imageResId = R.drawable.ic_fish,
                tagIds = listOf("fish", "vegetables", "protein"),
                canCook = false,
            ),
            recipe(
                id = 8,
                title = "Ягодный завтрак",
                status = "все ингредиенты в наличии",
                time = "7 мин",
                category = "завтрак",
                imageResId = null,
                tagIds = listOf("berries", "sour_milk", "sweet"),
                canCook = true,
                isFavorite = true,
            ),
        )

    private fun recipe(
        id: Int,
        title: String,
        status: String,
        time: String,
        category: String,
        imageResId: Int?,
        tagIds: List<String>,
        canCook: Boolean,
        isFavorite: Boolean = false,
    ): RecipeUi {
        val placeholder = recipePlaceholder()
        return RecipeUi(
            id = id,
            title = title,
            status = status,
            time = time,
            category = category,
            imageResId = imageResId,
            placeholderIconResId = placeholder.iconResId,
            placeholderColorResId = placeholder.colorResId,
            canCook = canCook,
            tagIds = tagIds,
            isFavorite = isFavorite,
        )
    }

    private fun recipePlaceholder(): RecipePlaceholder =
        RecipePlaceholder(R.drawable.ic_recipe_salad, R.color.recipe_placeholder_background)

    private fun RecipeDto.toUi(): RecipeUi =
        recipe(
            id = id.toIntOrNull() ?: id.hashCode(),
            title = title,
            status = status ?: if (canCook) {
                "все ингредиенты в наличии"
            } else {
                description.orEmpty().ifBlank { "проверьте ингредиенты" }
            },
            time = time.orEmpty().ifBlank { "30 мин" },
            category = category.orEmpty().ifBlank { "рецепт" },
            imageResId = imageKey.toImageResId(),
            tagIds = tagIds,
            canCook = canCook,
            isFavorite = isFavorite,
        )

    private fun String?.toImageResId(): Int? =
        when (this) {
            "vegetables" -> R.drawable.ic_vegetables
            "cheese" -> R.drawable.ic_cheese
            "fish" -> R.drawable.ic_fish
            "pasta" -> R.drawable.ic_pasta
            "berries" -> R.drawable.ic_berries
            "grains" -> R.drawable.ic_grains
            "poultry" -> R.drawable.ic_poultry
            "milk" -> R.drawable.ic_milk
            else -> null
        }

    private data class RecipePlaceholder(
        val iconResId: Int,
        val colorResId: Int,
    )
}
