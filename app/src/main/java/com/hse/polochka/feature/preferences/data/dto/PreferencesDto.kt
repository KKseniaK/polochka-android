package com.hse.polochka.feature.preferences.data.dto

data class PreferencesDto(
    val likedTagIds: List<String>,
    val restrictedTagIds: List<String>,
    val filterMode: String = "warn",
)
