package com.hse.polochka.feature.recipes.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityRecipesBinding
import com.hse.polochka.feature.recipes.data.RecipeRepository
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagAdapter

class RecipesFragment : Fragment(R.layout.activity_recipes) {

    private var _binding: ActivityRecipesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var recipeRepository: RecipeRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityRecipesBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        recipeRepository = RecipeRepository(requireContext())

        setupTags()
        setupCanCookRecipes()
        setupPopularRecipes()
        setupClicks()
    }

    private fun setupTags() {
        binding.tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.tagsRecyclerView.adapter =
            RecipeTagAdapter(recipeRepository.getFilterTags())
    }

    private fun setupCanCookRecipes() {
        binding.canCookRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.canCookRecyclerView.adapter = RecipeAdapter(
            items = recipeRepository.getCanCookRecipes(preferencesStorage.getState()),
            displayMode = RecipeAdapter.DisplayMode.Compact,
        ) {
            openRecipeDetails()
        }
    }

    private fun setupPopularRecipes() {
        binding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.popularRecyclerView.adapter = RecipeAdapter(
            items = recipeRepository.getPopularRecipes(preferencesStorage.getState()),
            displayMode = RecipeAdapter.DisplayMode.Wide,
        ) {
            openRecipeDetails()
        }
    }

    private fun setupClicks() {
        binding.addRecipeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Добавление рецепта позже", Toast.LENGTH_SHORT).show()
        }

        binding.favoritesButton.setOnClickListener {
            Toast.makeText(requireContext(), "Избранные рецепты позже", Toast.LENGTH_SHORT).show()
        }

        binding.showAllButton.setOnClickListener {
            Toast.makeText(requireContext(), "Все рецепты уже ниже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openRecipeDetails() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeDetailsFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
