package com.hse.polochka.feature.onboarding.domain.repository

import com.hse.polochka.core.preferences.TagPreferenceState

interface OnboardingRepository {
    suspend fun getOnboardingState(): TagPreferenceState
    suspend fun completeOnboarding(state: TagPreferenceState): TagPreferenceState
}
