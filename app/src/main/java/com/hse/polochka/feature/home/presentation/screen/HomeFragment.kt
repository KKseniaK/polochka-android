package com.hse.polochka.feature.home.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityHomeBinding
import com.hse.polochka.feature.home.presentation.adapter.HomeExpiringProductsAdapter
import com.hse.polochka.feature.home.presentation.adapter.HomeFamilyAdapter
import com.hse.polochka.feature.home.presentation.model.HomeExpiringProductUi
import com.hse.polochka.feature.home.presentation.model.HomeFamilyMemberUi
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment
import com.hse.polochka.feature.profile.presentation.screen.ProfileSettingsFragment
import com.hse.polochka.feature.recipes.data.RecipeRepository
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.screen.RecipeDetailsFragment
import com.hse.polochka.feature.recipes.presentation.screen.RecipesFragment

class HomeFragment : Fragment(R.layout.activity_home) {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var recipeRepository: RecipeRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityHomeBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        recipeRepository = RecipeRepository(requireContext())

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
                        name = "Папа",
                        message = "купите туалетную бумагу пж",
                        avatarResId = R.drawable.ic_profile_placeholder
                    ),
                    HomeFamilyMemberUi(
                        id = 2,
                        name = "Сестра",
                        message = null,
                        avatarResId = R.drawable.ic_profile_placeholder
                    ),
                    HomeFamilyMemberUi(
                        id = 3,
                        name = "Мама",
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
            RecipeAdapter(recipeRepository.getHomeRecipes(preferencesStorage.getState())) {
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
