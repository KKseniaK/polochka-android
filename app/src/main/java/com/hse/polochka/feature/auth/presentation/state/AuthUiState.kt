package com.hse.polochka.feature.auth.presentation.state

import com.hse.polochka.feature.auth.domain.model.User

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
