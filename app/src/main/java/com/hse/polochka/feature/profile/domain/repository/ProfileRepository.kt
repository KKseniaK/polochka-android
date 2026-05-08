package com.hse.polochka.feature.profile.domain.repository

import com.hse.polochka.feature.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(displayName: String, email: String): UserProfile
}
