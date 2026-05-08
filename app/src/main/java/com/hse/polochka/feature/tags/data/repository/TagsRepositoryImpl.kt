package com.hse.polochka.feature.tags.data.repository

import com.hse.polochka.core.network.requireBody
import com.hse.polochka.feature.tags.data.dto.TagDto
import com.hse.polochka.feature.tags.data.remote.TagsApi
import com.hse.polochka.feature.tags.domain.repository.TagsRepository

class TagsRepositoryImpl(
    private val tagsApi: TagsApi,
) : TagsRepository {

    override suspend fun getTags(): List<TagDto> =
        tagsApi.getTags().requireBody()
}
