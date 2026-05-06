package com.hse.polochka.feature.home.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityHomeBinding
import com.hse.polochka.feature.home.presentation.adapter.HomeExpiringProductsAdapter
import com.hse.polochka.feature.home.presentation.adapter.HomeFamilyAdapter
import com.hse.polochka.feature.home.presentation.model.HomeExpiringProductUi
import com.hse.polochka.feature.home.presentation.model.HomeFamilyMemberUi
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment
import com.hse.polochka.feature.profile.presentation.screen.ProfileSettingsFragment
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi
import com.hse.polochka.feature.recipes.presentation.screen.RecipeDetailsFragment
import com.hse.polochka.feature.recipes.presentation.screen.RecipesFragment

class HomeFragment : Fragment(R.layout.activity_home) {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityHomeBinding.bind(view)

        setupClicks()
        setupFamilyBlock()
        setupExpiringProductsBlock()
        setupStatsBlock()
        setupRecipeBlock()
    }

    private fun setupClicks() {
        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileSettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupFamilyBlock() {
        binding.familyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.familyRecyclerView.adapter =
            HomeFamilyAdapter(
                items = listOf(
                    HomeFamilyMemberUi(
                        id = 1,
                        name = "папа",
                        message = "купите туалетную бумагу плж",
                        avatarResId = R.drawable.ic_profile_placeholder
                    ),
                    HomeFamilyMemberUi(
                        id = 2,
                        name = "сестра",
                        message = null,
                        avatarResId = R.drawable.ic_profile_placeholder
                    ),
                    HomeFamilyMemberUi(
                        id = 3,
                        name = "мама",
                        message = "хочу приготовить щи",
                        avatarResId = R.drawable.ic_profile_placeholder
                    )
                ),
                onAddClick = {
                    InviteMemberDialogFragment()
                        .show(parentFragmentManager, "invite_member")
                }
            )
    }

    private fun setupExpiringProductsBlock() {
        binding.expiringProductsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.expiringProductsRecyclerView.adapter =
            HomeExpiringProductsAdapter(
                listOf(
                    HomeExpiringProductUi(1, "йогурт", "> 1 дн", R.drawable.ic_milk),
                    HomeExpiringProductUi(2, "молоко", "> 1 дн", R.drawable.ic_milk),
                    HomeExpiringProductUi(3, "йогурт", "> 1 дн", R.drawable.ic_milk),
                    HomeExpiringProductUi(4, "сыр", "> 1 дн", R.drawable.ic_cheese)
                )
            )
    }

    private fun setupStatsBlock() {
        binding.monthStatsBlock.savedProductsValueTextView.text = "25%"
        binding.monthStatsBlock.savedMoneyValueTextView.text = "5000 руб"
    }

    private fun setupRecipeBlock() {
        binding.homeRecipesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeRecipesRecyclerView.adapter =
            RecipeAdapter(getHomeRecipes()) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RecipeDetailsFragment())
                    .addToBackStack(null)
                    .commit()
            }

        binding.homeShowAllRecipesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecipesFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getHomeRecipes(): List<RecipeUi> {
        return listOf(
            RecipeUi(
                id = 1,
                title = "Щи",
                status = "все ингредиенты в наличии!",
                time = "30 мин",
                category = "суп",
                imageResId = R.drawable.ic_milk,
                isFavorite = false
            ),
            RecipeUi(
                id = 2,
                title = "Сырники",
                status = "все ингредиенты в наличии!",
                time = "20 мин",
                category = "завтрак",
                imageResId = R.drawable.ic_cheese,
                isFavorite = true
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}