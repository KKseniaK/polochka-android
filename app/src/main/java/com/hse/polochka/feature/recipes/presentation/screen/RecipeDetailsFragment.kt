package com.hse.polochka.feature.recipes.presentation.screen

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityRecipeDetailsBinding
import com.hse.polochka.feature.recipes.data.RecipeRepository
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeIngredientAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeStepAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeDetailsUi
import com.hse.polochka.feature.recipes.presentation.model.RecipeIngredientUi
import kotlinx.coroutines.launch

class RecipeDetailsFragment : Fragment(R.layout.activity_recipe_details) {

    private var _binding: ActivityRecipeDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var recipeRepository: RecipeRepository
    private lateinit var ingredientAdapter: RecipeIngredientAdapter
    private var recipeId: Int = 0
    private var isFavorite = false
    private var ingredients = emptyList<RecipeIngredientUi>()
    private var portionCount = 1
    private var hasAddedMissingToShopping = false
    private var isAddingMissingToShopping = false
    private var hasShownPortionWarning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeId = requireArguments().getInt(ARG_RECIPE_ID)
        hasAddedMissingToShopping = savedInstanceState?.getBoolean(KEY_ADDED_MISSING) ?: false
        hasShownPortionWarning = savedInstanceState?.getBoolean(KEY_PORTION_WARNING_SHOWN) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityRecipeDetailsBinding.bind(view)
        recipeRepository = RecipeRepository(requireContext())

        setupStaticClicks()
        setupRatingClicks()
        loadRecipe()
    }

    private fun loadRecipe() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                recipeRepository.getRecipeDetails(recipeId)
            }.onSuccess(::renderRecipe)
                .onFailure {
                    Toast.makeText(requireContext(), "Не удалось загрузить рецепт", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
        }
    }

    private fun renderRecipe(recipe: RecipeDetailsUi) {
        isFavorite = recipe.isFavorite
        ingredients = recipe.ingredients.sortedWith(
            compareBy<RecipeIngredientUi> { it.kind != "product" || !it.isRequiredForAvailability }
                .thenBy { !it.isAvailable }
                .thenBy { it.name.lowercase() }
        )

        binding.titleTextView.text = recipe.title
        binding.timeTextView.text = "время приготовления: ${recipe.time}"
        setRecipeImage(recipe.imageResId)
        updateFavoriteUi()

        binding.tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.tagsRecyclerView.adapter = RecipeTagAdapter(recipe.tagIds)

        ingredientAdapter = RecipeIngredientAdapter(ingredients)
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientsRecyclerView.adapter = ingredientAdapter

        binding.missingTextView.text = getString(R.string.recipe_missing_ingredients, recipe.missingCount)
        binding.missingTextView.isVisible = recipe.missingCount > 0
        binding.addToShoppingListButton.isVisible = recipe.missingCount > 0
        updatePortionsUi()

        binding.stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.stepsRecyclerView.adapter = RecipeStepAdapter(recipe.steps.toMutableList())
    }

    private fun setRecipeImage(imageResId: Int?) {
        if (imageResId == null) {
            binding.recipeImageView.setImageDrawable(null)
            binding.recipeImageView.setBackgroundResource(R.drawable.bg_recipe_image_placeholder)
        } else {
            binding.recipeImageView.setImageResource(imageResId)
        }
    }

    private fun setupStaticClicks() {
        binding.backButton.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteUi()
            Toast.makeText(
                requireContext(),
                if (isFavorite) "Добавлено в избранное" else "Удалено из избранного",
                Toast.LENGTH_SHORT,
            ).show()
        }

        binding.addToShoppingListButton.setOnClickListener {
            if (isAddingMissingToShopping) return@setOnClickListener
            if (hasAddedMissingToShopping) {
                showAddAgainDialog()
            } else {
                addMissingToShopping()
            }
        }

        binding.increasePortionButton.setOnClickListener {
            if (allRequiredProductsAvailable() && !hasShownPortionWarning) {
                hasShownPortionWarning = true
                showPortionWarning()
            }
            portionCount++
            updatePortionsUi()
        }

        binding.decreasePortionButton.setOnClickListener {
            if (portionCount > 1) {
                portionCount--
                updatePortionsUi()
            }
        }
    }

    private fun showPortionWarning() {
        Toast.makeText(
            requireContext(),
            "Вы увеличиваете количество ингредиентов. Полочка пока не следит за всеми граммовками, отследите это сами.",
            Toast.LENGTH_LONG,
        ).show()
    }

    private fun allRequiredProductsAvailable(): Boolean =
        ingredients
            .filter { it.kind == "product" && it.isRequiredForAvailability }
            .all { it.isAvailable }

    private fun showAddAgainDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Добавить ещё раз?")
            .setMessage("Вы уже добавили недостающие продукты для этого рецепта в список покупок. Добавить ещё?")
            .setPositiveButton("Да") { _, _ ->
                addMissingToShopping()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun addMissingToShopping() {
        isAddingMissingToShopping = true
        binding.addToShoppingListButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                recipeRepository.addMissingToShopping(recipeId)
            }.onSuccess { addedCount ->
                hasAddedMissingToShopping = addedCount > 0 || hasAddedMissingToShopping
                Toast.makeText(
                    requireContext(),
                    if (addedCount > 0) "Недостающие ингредиенты добавлены в список покупок" else "Добавлять нечего",
                    Toast.LENGTH_SHORT,
                ).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Не удалось добавить ингредиенты", Toast.LENGTH_SHORT).show()
            }

            isAddingMissingToShopping = false
            binding.addToShoppingListButton.isEnabled = true
        }
    }

    private fun updateFavoriteUi() {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart
        )
    }

    private fun updatePortionsUi() {
        binding.portionCountTextView.text = portionCount.toString()
        if (::ingredientAdapter.isInitialized) {
            ingredientAdapter.updatePortionCount(portionCount)
        }
    }

    private fun setupRatingClicks() {
        binding.ratingBlock.badRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Спасибо, учтем оценку", Toast.LENGTH_SHORT).show()
        }

        binding.ratingBlock.neutralRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Спасибо, учтем оценку", Toast.LENGTH_SHORT).show()
        }

        binding.ratingBlock.goodRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Спасибо, учтем оценку", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_ADDED_MISSING, hasAddedMissingToShopping)
        outState.putBoolean(KEY_PORTION_WARNING_SHOWN, hasShownPortionWarning)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"
        private const val KEY_ADDED_MISSING = "added_missing"
        private const val KEY_PORTION_WARNING_SHOWN = "portion_warning_shown"

        fun newInstance(recipeId: Int): RecipeDetailsFragment =
            RecipeDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPE_ID, recipeId)
                }
            }
    }
}
