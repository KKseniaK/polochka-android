package com.hse.polochka.feature.recipes.data.remote

import com.hse.polochka.feature.recipes.data.dto.RecipeDto
import com.hse.polochka.feature.recipes.data.dto.MissingToShoppingDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RecipesApi {

    @GET("recipes")
    suspend fun getRecipes(
        @Header("Authorization") authorization: String,
    ): Response<List<RecipeDto>>

    @GET("recipes/{recipeId}")
    suspend fun getRecipe(
        @Header("Authorization") authorization: String,
        @Path("recipeId") recipeId: Int,
    ): Response<RecipeDto>

    @POST("recipes/{recipeId}/missing-to-shopping")
    suspend fun addMissingToShopping(
        @Header("Authorization") authorization: String,
        @Path("recipeId") recipeId: Int,
    ): Response<MissingToShoppingDto>
}
