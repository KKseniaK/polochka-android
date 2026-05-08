package com.hse.polochka.core.preferences

data class TagPreferenceState(
    val likedTagIds: List<String> = emptyList(),
    val restrictedTagIds: List<String> = emptyList(),
    val completedOnboarding: Boolean = false,
)
