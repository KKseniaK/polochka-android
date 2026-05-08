package com.hse.polochka.feature.family.domain.repository

import com.hse.polochka.core.family.FamilyMember

interface FamilyRepository {
    suspend fun getFamilyName(): String
    suspend fun getMembers(): List<FamilyMember>
    suspend fun inviteMember(email: String): String
    suspend fun acceptInvitation(inviteToken: String): List<FamilyMember>
    suspend fun removeMember(memberId: String): Boolean
}
