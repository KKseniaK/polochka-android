package com.hse.polochka.feature.onboarding.data.dto

data class CompleteOnboardingRequestDto(
    val likedTagIds: List<String>,
    val restrictedTagIds: List<String>,
    val filterMode: String = "warn",
)

data class OnboardingStateDto(
    val completedOnboarding: Boolean,
    val likedTagIds: List<String>,
    val restrictedTagIds: List<String>,
    val filterMode: String = "warn",
)
