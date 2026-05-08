package com.hse.polochka.feature.auth.data.dto

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val displayName: String,
)
