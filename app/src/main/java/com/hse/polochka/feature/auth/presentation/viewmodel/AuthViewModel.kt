package com.hse.polochka.feature.auth.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hse.polochka.R
import com.hse.polochka.feature.auth.domain.model.User
import com.hse.polochka.feature.auth.domain.repository.AuthRepository
import com.hse.polochka.feature.auth.domain.usecase.LoginUseCase
import com.hse.polochka.feature.auth.domain.usecase.RegisterUseCase
import com.hse.polochka.feature.auth.presentation.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val context: Context,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        runAuthRequest {
            loginUseCase(email, password)
        }
    }

    fun register(email: String, password: String, displayName: String) {
        runAuthRequest {
            registerUseCase(email, password, displayName)
        }
    }

    fun restoreSession() {
        if (!repository.hasToken()) return
        runAuthRequest {
            repository.currentUser() ?: throw IllegalStateException(
                context.getString(R.string.auth_error_session_expired)
            )
        }
    }

    fun hasToken(): Boolean = repository.hasToken()

    fun logout() {
        repository.logout()
        _state.value = AuthUiState.Idle
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }

    private fun runAuthRequest(block: suspend () -> User) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            _state.value = runCatching { block() }
                .fold(
                    onSuccess = { AuthUiState.Authenticated(it) },
                    onFailure = {
                        AuthUiState.Error(
                            it.message ?: context.getString(R.string.auth_error_request_failed)
                        )
                    },
                )
        }
    }
}

class AuthViewModelFactory(
    private val context: Context,
    private val repository: AuthRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            context = context,
            loginUseCase = LoginUseCase(repository),
            registerUseCase = RegisterUseCase(repository),
            repository = repository,
        ) as T
    }
}
