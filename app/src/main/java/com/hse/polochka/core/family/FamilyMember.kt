package com.hse.polochka.core.family

data class FamilyMember(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val isCurrentUser: Boolean,
)
