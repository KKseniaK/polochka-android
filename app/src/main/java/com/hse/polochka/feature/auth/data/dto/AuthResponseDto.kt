package com.hse.polochka.feature.auth.data.dto

data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

data class UserDto(
    val id: String,
    val email: String,
    val displayName: String?,
)

data class ErrorResponseDto(
    val message: String?,
)
