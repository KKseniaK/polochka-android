package com.hse.polochka.feature.profile.data.dto

data class ProfileDto(
    val id: String,
    val email: String,
    val displayName: String?,
    val familyId: String?,
)

data class UpdateProfileRequestDto(
    val displayName: String,
    val email: String,
)
