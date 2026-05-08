package com.hse.polochka.feature.analytics.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.feature.analytics.data.remote.AnalyticsApi
import com.hse.polochka.feature.analytics.data.repository.AnalyticsRepositoryImpl
import com.hse.polochka.feature.analytics.domain.repository.AnalyticsRepository
import com.hse.polochka.feature.analytics.domain.usecase.GetMonthlyAnalyticsUseCase
import com.hse.polochka.feature.analytics.presentation.state.AnalyticsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val getMonthlyAnalyticsUseCase: GetMonthlyAnalyticsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    fun load(month: String) {
        viewModelScope.launch {
            _state.value = AnalyticsUiState.Loading
            _state.value = runCatching { getMonthlyAnalyticsUseCase(month) }
                .fold(
                    onSuccess = { AnalyticsUiState.Content(it) },
                    onFailure = {
                        AnalyticsUiState.Error(
                            it.message ?: "Не удалось загрузить статистику"
                        )
                    },
                )
        }
    }
}

class AnalyticsViewModelFactory(
    context: Context,
    private val repository: AnalyticsRepository = AnalyticsRepositoryImpl(
        analyticsApi = ApiClient.create(AnalyticsApi::class.java),
        eventStorage = StorageEventStorage(context.applicationContext),
        authHeaderProvider = AuthHeaderProvider(UserSessionStorage(context.applicationContext)),
    ),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalyticsViewModel(
            getMonthlyAnalyticsUseCase = GetMonthlyAnalyticsUseCase(repository),
        ) as T
    }
}
