package com.hse.polochka.feature.family.data.repository

import com.hse.polochka.core.family.FamilyMember
import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.feature.family.data.dto.FamilyMemberDto
import com.hse.polochka.feature.family.data.dto.InviteFamilyMemberRequestDto
import com.hse.polochka.feature.family.data.remote.FamilyApi
import com.hse.polochka.feature.family.domain.repository.FamilyRepository
import java.io.IOException

class FamilyRepositoryImpl(
    private val familyApi: FamilyApi,
    private val familyStorage: FamilyStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : FamilyRepository {

    override suspend fun getFamilyName(): String =
        runCatching {
            familyApi.getFamily(authHeaderProvider.bearer()).requireBody().name
        }.getOrElse { error ->
            if (error is IOException) familyStorage.getFamilyName() else throw error
        }

    override suspend fun getMembers(): List<FamilyMember> =
        runCatching {
            familyApi.getFamily(authHeaderProvider.bearer()).requireBody().members.map { it.toDomain() }
        }.getOrElse { error ->
            if (error is IOException) familyStorage.getMembers() else throw error
        }

    override suspend fun inviteMember(email: String): String =
        runCatching {
            familyApi.inviteMember(
                authorization = authHeaderProvider.bearer(),
                request = InviteFamilyMemberRequestDto(email = email.trim()),
            ).requireBody().inviteLink
        }.getOrElse { error ->
            if (error is IOException) {
                familyStorage.addInvitedMember(email)
                familyStorage.createInviteLink(email)
            } else {
                throw error
            }
        }

    override suspend fun acceptInvitation(inviteToken: String): List<FamilyMember> =
        runCatching {
            familyApi.acceptInvitation(authHeaderProvider.bearer(), inviteToken)
                .requireBody()
                .members
                .map { it.toDomain() }
        }.getOrElse { error ->
            if (error is IOException) familyStorage.getMembers() else throw error
        }

    override suspend fun removeMember(memberId: String): Boolean =
        runCatching {
            val response = familyApi.removeMember(authHeaderProvider.bearer(), memberId)
            if (!response.isSuccessful) {
                throw IllegalStateException("Server request failed: HTTP ${response.code()}")
            }
            true
        }.getOrElse { error ->
            if (error is IOException) familyStorage.removeMember(memberId) else throw error
        }

    private fun FamilyMemberDto.toDomain(): FamilyMember =
        FamilyMember(
            id = id,
            name = name,
            email = email,
            role = role,
            status = status,
            isCurrentUser = isCurrentUser,
        )
}
