package com.hse.polochka.feature.recipes.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityRecipesBinding
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class RecipesFragment : Fragment(R.layout.activity_recipes) {

    private var _binding: ActivityRecipesBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityRecipesBinding.bind(view)

        setupTags()
        setupCanCookRecipes()
        setupPopularRecipes()
        setupClicks()
    }

    private fun setupTags() {
        binding.tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.tagsRecyclerView.adapter =
            RecipeTagAdapter(
                listOf("сытно", "быстро", "супы", "каши", "завтрак")
            )
    }

    private fun setupCanCookRecipes() {
        binding.canCookRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.canCookRecyclerView.adapter =
            RecipeAdapter(getCanCookRecipes()) {
                openRecipeDetails()
            }
    }

    private fun setupPopularRecipes() {
        binding.popularRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.popularRecyclerView.adapter =
            RecipeAdapter(getPopularRecipes()) {
                openRecipeDetails()
            }
    }

    private fun setupClicks() {
        binding.addRecipeButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Добавление рецепта позже",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.favoritesButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Избранные рецепты позже",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.showAllButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Все рецепты позже",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openRecipeDetails() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeDetailsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun getCanCookRecipes(): List<RecipeUi> {
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
            ),
            RecipeUi(
                id = 3,
                title = "Щи",
                status = "все ингредиенты в наличии!",
                time = "30 мин",
                category = "суп",
                imageResId = R.drawable.ic_milk,
                isFavorite = false
            ),
            RecipeUi(
                id = 4,
                title = "Щи",
                status = "все ингредиенты в наличии!",
                time = "30 мин",
                category = "суп",
                imageResId = R.drawable.ic_milk,
                isFavorite = false
            ),

        )
    }

    private fun getPopularRecipes(): List<RecipeUi> {
        return listOf(
            RecipeUi(
                id = 3,
                title = "Рамен",
                status = "не хватает 2 ингредиентов!",
                time = "40 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                isFavorite = false
            ),
            RecipeUi(
                id = 4,
                title = "Паста карбонара",
                status = "не хватает 3 ингредиентов!",
                time = "15 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                isFavorite = false
            ),
            RecipeUi(
                id = 3,
                title = "Рамен",
                status = "не хватает 2 ингредиентов!",
                time = "40 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                isFavorite = false
            ),
            RecipeUi(
                id = 3,
                title = "Рамен",
                status = "не хватает 2 ингредиентов!",
                time = "40 мин",
                category = "сытно",
                imageResId = R.drawable.ic_pasta,
                isFavorite = false
            ),
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}