package com.hse.polochka.feature.onboarding.presentation.state

data class OnboardingUiState(
    val selectedCategoryIds: List<Int> = emptyList(),
    val likedProductIds: List<Int> = emptyList(),
    val dislikedProductIds: List<Int> = emptyList()
)