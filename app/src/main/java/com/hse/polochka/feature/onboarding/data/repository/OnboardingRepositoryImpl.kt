package com.hse.polochka.feature.onboarding.data.repository

import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.onboarding.data.dto.CompleteOnboardingRequestDto
import com.hse.polochka.feature.onboarding.data.dto.OnboardingStateDto
import com.hse.polochka.feature.onboarding.data.remote.OnboardingApi
import com.hse.polochka.feature.onboarding.domain.repository.OnboardingRepository
import java.io.IOException

class OnboardingRepositoryImpl(
    private val onboardingApi: OnboardingApi,
    private val preferencesStorage: PreferencesStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : OnboardingRepository {

    override suspend fun getOnboardingState(): TagPreferenceState =
        runCatching {
            onboardingApi.getOnboardingState(authHeaderProvider.bearer()).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) preferencesStorage.getState() else throw error
        }

    override suspend fun completeOnboarding(state: TagPreferenceState): TagPreferenceState {
        preferencesStorage.saveLikedTagIds(state.likedTagIds)
        preferencesStorage.saveRestrictedTagIds(state.restrictedTagIds)
        preferencesStorage.markOnboardingCompleted()

        return runCatching {
            onboardingApi.completeOnboarding(
                authorization = authHeaderProvider.bearer(),
                request = CompleteOnboardingRequestDto(
                    likedTagIds = state.likedTagIds,
                    restrictedTagIds = state.restrictedTagIds,
                ),
            ).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                preferencesStorage.getState()
            } else {
                throw error
            }
        }
    }

    private fun OnboardingStateDto.toDomain(): TagPreferenceState =
        TagPreferenceState(
            likedTagIds = likedTagIds,
            restrictedTagIds = restrictedTagIds,
            completedOnboarding = completedOnboarding,
        )
}
