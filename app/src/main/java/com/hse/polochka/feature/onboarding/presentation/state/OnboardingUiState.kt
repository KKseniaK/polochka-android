package com.hse.polochka.feature.onboarding.presentation.state

data class OnboardingUiState(
    val selectedCategoryIds: List<String> = emptyList(),
    val likedProductIds: List<String> = emptyList(),
    val dislikedProductIds: List<String> = emptyList()
)
