package com.hse.polochka.feature.preferences.domain.repository

import com.hse.polochka.core.preferences.TagPreferenceState

interface PreferencesRepository {
    suspend fun getPreferences(): TagPreferenceState
    suspend fun updatePreferences(state: TagPreferenceState): TagPreferenceState
    fun getLocalPreferences(): TagPreferenceState
}
