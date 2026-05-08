package com.hse.polochka.feature.preferences.data.repository

import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.network.requireBody
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.feature.preferences.data.dto.PreferencesDto
import com.hse.polochka.feature.preferences.data.remote.PreferencesApi
import com.hse.polochka.feature.preferences.domain.repository.PreferencesRepository
import java.io.IOException

class PreferencesRepositoryImpl(
    private val preferencesApi: PreferencesApi,
    private val preferencesStorage: PreferencesStorage,
    private val authHeaderProvider: AuthHeaderProvider,
) : PreferencesRepository {

    override suspend fun getPreferences(): TagPreferenceState =
        runCatching {
            preferencesApi.getPreferences(authHeaderProvider.bearer()).requireBody().toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                preferencesStorage.getState()
            } else {
                throw error
            }
        }

    override suspend fun updatePreferences(state: TagPreferenceState): TagPreferenceState {
        saveLocal(state)
        return runCatching {
            preferencesApi.updatePreferences(authHeaderProvider.bearer(), state.toDto())
                .requireBody()
                .toDomain()
        }.getOrElse { error ->
            if (error is IOException) {
                state
            } else {
                throw error
            }
        }
    }

    override fun getLocalPreferences(): TagPreferenceState = preferencesStorage.getState()

    private fun saveLocal(state: TagPreferenceState) {
        preferencesStorage.saveLikedTagIds(state.likedTagIds)
        preferencesStorage.saveRestrictedTagIds(state.restrictedTagIds)
        if (state.completedOnboarding) {
            preferencesStorage.markOnboardingCompleted()
        }
    }

    private fun PreferencesDto.toDomain(): TagPreferenceState =
        TagPreferenceState(
            likedTagIds = likedTagIds,
            restrictedTagIds = restrictedTagIds,
            completedOnboarding = preferencesStorage.getState().completedOnboarding,
        )

    private fun TagPreferenceState.toDto(): PreferencesDto =
        PreferencesDto(
            likedTagIds = likedTagIds,
            restrictedTagIds = restrictedTagIds,
            filterMode = "warn",
        )
}
