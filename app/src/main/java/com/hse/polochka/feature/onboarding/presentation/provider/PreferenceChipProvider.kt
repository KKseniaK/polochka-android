package com.hse.polochka.feature.onboarding.presentation.provider

import com.hse.polochka.R
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi

object PreferenceChipProvider {

    fun getProductChips(selectedIds: List<Int> = emptyList()): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi(101, R.string.pref_milk, R.drawable.ic_milk, 101 in selectedIds),
            PreferenceChipUi(102, R.string.pref_sour_milk, R.drawable.ic_sour_milk, 102 in selectedIds),
            PreferenceChipUi(103, R.string.pref_cheese, R.drawable.ic_cheese, 103 in selectedIds),
            PreferenceChipUi(104, R.string.pref_meat, R.drawable.ic_meat, 104 in selectedIds),
            PreferenceChipUi(105, R.string.pref_fish, R.drawable.ic_fish, 105 in selectedIds),
            PreferenceChipUi(106, R.string.pref_poultry, R.drawable.ic_poultry, 106 in selectedIds),
            PreferenceChipUi(107, R.string.pref_seafood, R.drawable.ic_seafood, 107 in selectedIds),
            PreferenceChipUi(108, R.string.pref_sausage, R.drawable.ic_sausage, 108 in selectedIds),
            PreferenceChipUi(109, R.string.pref_vegetables, R.drawable.ic_vegetables, 109 in selectedIds),
            PreferenceChipUi(110, R.string.pref_fruits, R.drawable.ic_fruits, 110 in selectedIds),
            PreferenceChipUi(111, R.string.pref_greens, R.drawable.ic_greens, 111 in selectedIds),
            PreferenceChipUi(112, R.string.pref_berries, R.drawable.ic_berries, 112 in selectedIds),
            PreferenceChipUi(113, R.string.pref_mushrooms, R.drawable.ic_mushrooms, 113 in selectedIds),
            PreferenceChipUi(114, R.string.pref_bakery, R.drawable.ic_bakery, 114 in selectedIds),
            PreferenceChipUi(115, R.string.pref_pasta, R.drawable.ic_pasta, 115 in selectedIds),
            PreferenceChipUi(116, R.string.pref_grains, R.drawable.ic_grains, 116 in selectedIds),
            PreferenceChipUi(117, R.string.pref_coffee, R.drawable.ic_coffee, 117 in selectedIds),
            PreferenceChipUi(118, R.string.pref_tea, R.drawable.ic_tea, 118 in selectedIds),
            PreferenceChipUi(119, R.string.pref_sweet, R.drawable.ic_sweet, 119 in selectedIds),
            PreferenceChipUi(120, R.string.pref_salty, R.drawable.ic_salty, 120 in selectedIds),
            PreferenceChipUi(121, R.string.pref_hot, R.drawable.ic_hot, 121 in selectedIds)
        )
    }
}