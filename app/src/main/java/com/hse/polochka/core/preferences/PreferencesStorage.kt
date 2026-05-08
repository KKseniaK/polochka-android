package com.hse.polochka.core.preferences

import android.content.Context

class PreferencesStorage(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getState(): TagPreferenceState =
        TagPreferenceState(
            likedTagIds = getStringList(KEY_LIKED_TAG_IDS),
            restrictedTagIds = getStringList(KEY_RESTRICTED_TAG_IDS),
            completedOnboarding = preferences.getBoolean(KEY_COMPLETED_ONBOARDING, false),
        )

    fun saveLikedTagIds(tagIds: List<String>) {
        saveStringList(KEY_LIKED_TAG_IDS, tagIds)
    }

    fun saveRestrictedTagIds(tagIds: List<String>) {
        saveStringList(KEY_RESTRICTED_TAG_IDS, tagIds)
    }

    fun markOnboardingCompleted() {
        preferences.edit()
            .putBoolean(KEY_COMPLETED_ONBOARDING, true)
            .apply()
    }

    private fun getStringList(key: String): List<String> =
        preferences.getStringSet(key, emptySet<String>())
            ?.toList()
            ?.sorted()
            .orEmpty()

    private fun saveStringList(key: String, values: List<String>) {
        preferences.edit()
            .putStringSet(key, values.toSet())
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "tag_preferences"
        private const val KEY_LIKED_TAG_IDS = "liked_tag_ids"
        private const val KEY_RESTRICTED_TAG_IDS = "restricted_tag_ids"
        private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"
    }
}
