package com.hse.polochka.feature.tags.data.remote

import com.hse.polochka.feature.tags.data.dto.TagDto
import retrofit2.Response
import retrofit2.http.GET

interface TagsApi {

    @GET("tags")
    suspend fun getTags(): Response<List<TagDto>>
}
