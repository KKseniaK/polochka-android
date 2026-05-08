package com.hse.polochka.feature.storage.data.remote

import com.hse.polochka.feature.storage.data.dto.CreateStorageProductRequestDto
import com.hse.polochka.feature.storage.data.dto.StorageEventDto
import com.hse.polochka.feature.storage.data.dto.StorageProductDto
import com.hse.polochka.feature.storage.data.dto.WriteOffStorageProductRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StorageApi {

    @GET("storage/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
    ): Response<List<StorageProductDto>>

    @POST("storage/products")
    suspend fun addProduct(
        @Header("Authorization") authorization: String,
        @Body request: CreateStorageProductRequestDto,
    ): Response<StorageProductDto>

    @POST("storage/products/{productId}/write-off")
    suspend fun writeOffProduct(
        @Header("Authorization") authorization: String,
        @Path("productId") productId: Int,
        @Body request: WriteOffStorageProductRequestDto,
    ): Response<StorageEventDto>
}
