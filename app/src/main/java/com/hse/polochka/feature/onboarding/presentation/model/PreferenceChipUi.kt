package com.hse.polochka.feature.onboarding.presentation.model

data class PreferenceChipUi(
    val id: Int,
    val titleResId: Int,
    val iconResId: Int? = null,
    val isSelected: Boolean = false
)