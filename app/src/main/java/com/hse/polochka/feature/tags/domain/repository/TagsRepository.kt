package com.hse.polochka.feature.tags.domain.repository

import com.hse.polochka.feature.tags.data.dto.TagDto

interface TagsRepository {
    suspend fun getTags(): List<TagDto>
}
