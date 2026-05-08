package com.hse.polochka.feature.recipes.data

import android.content.Context
import com.hse.polochka.R
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class RecipeRepository(private val context: Context) {

    fun getFilterTags(): List<String> =
        listOf("сытно", "быстро", "супы", "каши", "завтрак")

    fun getHomeRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes().take(3), preferences)

    fun getCanCookRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes().filter { it.id in listOf(1, 2, 3, 4) }, preferences)

    fun getPopularRecipes(preferences: TagPreferenceState): List<RecipeUi> =
        personalize(allRecipes().filter { it.id in listOf(5, 6, 7, 8) }, preferences)

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
            RecipeUi(
                id = 1,
                title = "Щи",
                status = "все ингредиенты в наличии!",
                time = "30 мин",
                category = "суп",
                imageResId = R.drawable.ic_vegetables,
                tagIds = listOf("vegetables", "greens"),
                isFavorite = false
            ),
            RecipeUi(
                id = 2,
                title = "Сырники",
                status = "все ингредиенты в наличии!",
                time = "20 мин",
                category = "завтрак",
                imageResId = R.drawable.ic_cheese,
                tagIds = listOf("cheese", "milk", "sweet"),
                isFavorite = true
            ),
            RecipeUi(
                id = 3,
                title = "Овощной салат",
                status = "все ингредиенты в наличии!",
                time = "10 мин",
                category = "быстро",
                imageResId = R.drawable.ic_vegetables,
                tagIds = listOf("vegetables", "greens", "diet"),
                isFavorite = false
            ),
            RecipeUi(
                id = 4,
                title = "Куриная каша",
                status = "не хватает 1 ингредиента!",
                time = "35 мин",
                category = "каша",
                imageResId = R.drawable.ic_poultry,
                tagIds = listOf("poultry", "grains", "protein"),
                isFavorite = false
            ),
            RecipeUi(
                id = 5,
                title = "Рамен",
                status = "не хватает 2 ингредиентов!",
                time = "40 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                tagIds = listOf("pasta", "meat", "hot"),
                isFavorite = false
            ),
            RecipeUi(
                id = 6,
                title = "Паста карбонара",
                status = "не хватает 3 ингредиентов!",
                time = "15 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                tagIds = listOf("pasta", "cheese", "meat"),
                isFavorite = false
            ),
            RecipeUi(
                id = 7,
                title = "Рыба с овощами",
                status = "не хватает 1 ингредиента!",
                time = "25 мин",
                category = "ужин",
                imageResId = R.drawable.ic_fish,
                tagIds = listOf("fish", "vegetables", "protein"),
                isFavorite = false
            ),
            RecipeUi(
                id = 8,
                title = "Ягодный завтрак",
                status = "все ингредиенты в наличии!",
                time = "7 мин",
                category = "завтрак",
                imageResId = R.drawable.ic_berries,
                tagIds = listOf("berries", "sour_milk", "sweet"),
                isFavorite = true
            ),
        )
}
