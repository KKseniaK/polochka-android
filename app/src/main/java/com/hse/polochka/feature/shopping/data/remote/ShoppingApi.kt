package com.hse.polochka.feature.shopping.data.remote

import com.hse.polochka.feature.shopping.data.dto.CreateShoppingItemRequestDto
import com.hse.polochka.feature.shopping.data.dto.DeleteShoppingItemRequestDto
import com.hse.polochka.feature.shopping.data.dto.MoveShoppingToStorageRequestDto
import com.hse.polochka.feature.shopping.data.dto.ShoppingHistoryDto
import com.hse.polochka.feature.shopping.data.dto.ShoppingItemDto
import com.hse.polochka.feature.shopping.data.dto.UpdateShoppingItemRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ShoppingApi {

    @GET("shopping/items")
    suspend fun getItems(
        @Header("Authorization") authorization: String,
    ): Response<List<ShoppingItemDto>>

    @POST("shopping/items")
    suspend fun addItem(
        @Header("Authorization") authorization: String,
        @Body request: CreateShoppingItemRequestDto,
    ): Response<ShoppingItemDto>

    @PATCH("shopping/items/{itemId}")
    suspend fun updateItem(
        @Header("Authorization") authorization: String,
        @Path("itemId") itemId: Int,
        @Body request: UpdateShoppingItemRequestDto,
    ): Response<ShoppingItemDto>

    @HTTP(method = "DELETE", path = "shopping/items/{itemId}", hasBody = true)
    suspend fun deleteItem(
        @Header("Authorization") authorization: String,
        @Path("itemId") itemId: Int,
        @Body request: DeleteShoppingItemRequestDto,
    ): Response<ShoppingItemDto>

    @POST("shopping/move-to-storage")
    suspend fun moveToStorage(
        @Header("Authorization") authorization: String,
        @Body request: MoveShoppingToStorageRequestDto,
    ): Response<List<ShoppingItemDto>>

    @GET("shopping/history")
    suspend fun getHistory(
        @Header("Authorization") authorization: String,
    ): Response<List<ShoppingHistoryDto>>
}
