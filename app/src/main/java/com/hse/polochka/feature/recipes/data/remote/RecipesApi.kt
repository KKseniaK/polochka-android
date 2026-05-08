package com.hse.polochka.feature.recipes.data.remote

import com.hse.polochka.feature.recipes.data.dto.RecipeDto
import retrofit2.Response
import retrofit2.http.GET

interface RecipesApi {

    @GET("recipes")
    suspend fun getRecipes(): Response<List<RecipeDto>>
}
