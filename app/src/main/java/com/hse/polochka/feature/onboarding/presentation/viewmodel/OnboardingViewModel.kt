package com.hse.polochka.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.hse.polochka.feature.onboarding.presentation.state.OnboardingUiState

class OnboardingViewModel : ViewModel() {

    private var _uiState = OnboardingUiState()
    val uiState: OnboardingUiState
        get() = _uiState

    fun saveSelectedCategories(categoryIds: List<Int>) {
        _uiState = _uiState.copy(
            selectedCategoryIds = categoryIds
        )
    }
}