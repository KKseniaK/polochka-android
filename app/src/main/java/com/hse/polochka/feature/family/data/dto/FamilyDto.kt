package com.hse.polochka.feature.family.data.dto

data class FamilyDto(
    val id: String,
    val name: String,
    val members: List<FamilyMemberDto>,
)

data class FamilyMemberDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val isCurrentUser: Boolean = false,
)

data class InviteFamilyMemberRequestDto(
    val email: String,
)

data class InviteFamilyMemberResponseDto(
    val inviteLink: String,
    val member: FamilyMemberDto,
)
