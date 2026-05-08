package com.hse.polochka.feature.onboarding.presentation.provider

import com.hse.polochka.R
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi

object PreferenceChipProvider {

    fun getRestrictionChips(selectedIds: List<String> = emptyList()): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi("gluten_free", R.string.pref_gluten_free, R.drawable.ic_glutenfree, "gluten_free" in selectedIds),
            PreferenceChipUi("lactose_free", R.string.pref_lactose_free, R.drawable.ic_lactosefree, "lactose_free" in selectedIds),
            PreferenceChipUi("vegan", R.string.pref_vegan, R.drawable.ic_vegan, "vegan" in selectedIds),
            PreferenceChipUi("vegetarian", R.string.pref_vegetarian, R.drawable.ic_vegetarian, "vegetarian" in selectedIds),
            PreferenceChipUi("protein", R.string.pref_protein, R.drawable.ic_protein, "protein" in selectedIds),
            PreferenceChipUi("diet", R.string.pref_diet, isSelected = "diet" in selectedIds),
            PreferenceChipUi("fatty", R.string.pref_fatty, isSelected = "fatty" in selectedIds),
            PreferenceChipUi("spicy", R.string.pref_spicy, isSelected = "spicy" in selectedIds),
            PreferenceChipUi("sugar_free", R.string.pref_sugar_free, isSelected = "sugar_free" in selectedIds),
            PreferenceChipUi("halal", R.string.pref_halal, R.drawable.ic_halal, "halal" in selectedIds),
        )
    }

    fun getProductChips(selectedIds: List<String> = emptyList()): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi("milk", R.string.pref_milk, R.drawable.ic_milk, "milk" in selectedIds),
            PreferenceChipUi("sour_milk", R.string.pref_sour_milk, R.drawable.ic_sour_milk, "sour_milk" in selectedIds),
            PreferenceChipUi("cheese", R.string.pref_cheese, R.drawable.ic_cheese, "cheese" in selectedIds),
            PreferenceChipUi("meat", R.string.pref_meat, R.drawable.ic_meat, "meat" in selectedIds),
            PreferenceChipUi("fish", R.string.pref_fish, R.drawable.ic_fish, "fish" in selectedIds),
            PreferenceChipUi("poultry", R.string.pref_poultry, R.drawable.ic_poultry, "poultry" in selectedIds),
            PreferenceChipUi("seafood", R.string.pref_seafood, R.drawable.ic_seafood, "seafood" in selectedIds),
            PreferenceChipUi("sausage", R.string.pref_sausage, R.drawable.ic_sausage, "sausage" in selectedIds),
            PreferenceChipUi("vegetables", R.string.pref_vegetables, R.drawable.ic_vegetables, "vegetables" in selectedIds),
            PreferenceChipUi("fruits", R.string.pref_fruits, R.drawable.ic_fruits, "fruits" in selectedIds),
            PreferenceChipUi("greens", R.string.pref_greens, R.drawable.ic_greens, "greens" in selectedIds),
            PreferenceChipUi("berries", R.string.pref_berries, R.drawable.ic_berries, "berries" in selectedIds),
            PreferenceChipUi("mushrooms", R.string.pref_mushrooms, R.drawable.ic_mushrooms, "mushrooms" in selectedIds),
            PreferenceChipUi("bakery", R.string.pref_bakery, R.drawable.ic_bakery, "bakery" in selectedIds),
            PreferenceChipUi("pasta", R.string.pref_pasta, R.drawable.ic_pasta, "pasta" in selectedIds),
            PreferenceChipUi("grains", R.string.pref_grains, R.drawable.ic_grains, "grains" in selectedIds),
            PreferenceChipUi("coffee", R.string.pref_coffee, R.drawable.ic_coffee, "coffee" in selectedIds),
            PreferenceChipUi("tea", R.string.pref_tea, R.drawable.ic_tea, "tea" in selectedIds),
            PreferenceChipUi("sweet", R.string.pref_sweet, R.drawable.ic_sweet, "sweet" in selectedIds),
            PreferenceChipUi("salty", R.string.pref_salty, R.drawable.ic_salty, "salty" in selectedIds),
            PreferenceChipUi("hot", R.string.pref_hot, R.drawable.ic_hot, "hot" in selectedIds)
        )
    }
}
