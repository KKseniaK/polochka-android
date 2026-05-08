package com.hse.polochka.feature.profile.data.repository

import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.feature.profile.data.dto.ProfileDto
import com.hse.polochka.feature.profile.data.dto.UpdateProfileRequestDto
import com.hse.polochka.feature.profile.data.remote.ProfileApi
import com.hse.polochka.feature.profile.domain.model.UserProfile
import com.hse.polochka.feature.profile.domain.repository.ProfileRepository
import java.io.IOException

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val familyStorage: FamilyStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : ProfileRepository {

    override suspend fun getProfile(): UserProfile =
        runCatching {
            profileApi.getProfile(authHeaderProvider.bearer()).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                familyStorage.getMembers().first { it.isCurrentUser }.let { member ->
                    UserProfile(
                        id = member.id,
                        email = member.email,
                        displayName = member.name,
                        familyId = LOCAL_FAMILY_ID,
                    )
                }
            } else {
                throw error
            }
        }

    override suspend fun updateProfile(displayName: String, email: String): UserProfile {
        familyStorage.updateCurrentUser(name = displayName, email = email)
        return runCatching {
            profileApi.updateProfile(
                authorization = authHeaderProvider.bearer(),
                request = UpdateProfileRequestDto(displayName = displayName, email = email),
            ).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                getProfile()
            } else {
                throw error
            }
        }
    }

    private fun ProfileDto.toDomain(): UserProfile =
        UserProfile(
            id = id,
            email = email,
            displayName = displayName.orEmpty(),
            familyId = familyId,
        )

    private companion object {
        const val LOCAL_FAMILY_ID = "local-family"
    }
}
