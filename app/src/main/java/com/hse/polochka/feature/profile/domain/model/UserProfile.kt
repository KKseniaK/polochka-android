package com.hse.polochka.feature.profile.domain.model

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val familyId: String?,
)
