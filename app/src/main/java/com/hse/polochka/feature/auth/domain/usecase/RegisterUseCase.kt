package com.hse.polochka.feature.auth.domain.usecase

import com.hse.polochka.feature.auth.domain.model.User
import com.hse.polochka.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String, displayName: String): User =
        repository.register(email, password, displayName)
}
