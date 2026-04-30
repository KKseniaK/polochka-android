package com.hse.polochka.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hse.polochka.feature.onboarding.presentation.state.OnboardingUiState

class OnboardingViewModel : ViewModel() {

    val userName = MutableLiveData<String>()

    private var _uiState = OnboardingUiState()
    val uiState: OnboardingUiState
        get() = _uiState

    fun saveSelectedCategories(categoryIds: List<Int>) {
        _uiState = _uiState.copy(
            selectedCategoryIds = categoryIds
        )
    }

    fun saveLikedProducts(productIds: List<Int>) {
        _uiState = _uiState.copy(
            likedProductIds = productIds
        )
    }

    fun saveDislikedProducts(productIds: List<Int>) {
        _uiState = _uiState.copy(
            dislikedProductIds = productIds
        )
    }

    fun saveFamilySettings(
        userName: String,
        avatarUri: String?
    ) {
        // пока просто заглушка, потом добавим в UiState / отправку на backend
    }
}