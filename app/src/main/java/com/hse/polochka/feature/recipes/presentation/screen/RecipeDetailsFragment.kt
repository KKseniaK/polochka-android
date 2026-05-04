package com.hse.polochka.feature.recipes.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityRecipeDetailsBinding
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeIngredientAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeStepAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeIngredientUi
import com.hse.polochka.feature.recipes.presentation.model.RecipeStepUi

class RecipeDetailsFragment : Fragment(R.layout.activity_recipe_details) {

    private var _binding: ActivityRecipeDetailsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var isFavorite = false

    private lateinit var ingredientAdapter: RecipeIngredientAdapter

    private var portionCount = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityRecipeDetailsBinding.bind(view)

        setMockRecipeData()
        setupTags()
        setupIngredients()
        setupClicks()
        setupRatingClicks()
        setupSteps()
    }

    private fun setMockRecipeData() {
        binding.titleTextView.text = "Паста карбонара"
        binding.timeTextView.text = "время приготовления: 15 минут"

        setRecipeImage(R.drawable.ic_pasta)
    }

    private fun setRecipeImage(imageResId: Int?) {
        if (imageResId == null) {
            binding.recipeImageView.setImageDrawable(null)
            binding.recipeImageView.setBackgroundResource(R.drawable.bg_recipe_image_placeholder)
        } else {
            binding.recipeImageView.setImageResource(imageResId)
        }
    }

    private fun setupTags() {
        binding.tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.tagsRecyclerView.adapter =
            RecipeTagAdapter(
                listOf("сытно", "быстро", "быстро")
            )
    }

    private fun setupIngredients() {
        val ingredients = listOf(
            RecipeIngredientUi(1, "Паста сухая", "100 г", true),
            RecipeIngredientUi(2, "Бекон или панчетта", "80–100 г", false),
            RecipeIngredientUi(3, "Сыр пармезан", "50 г", true),
            RecipeIngredientUi(4, "Сливки 10–20%", "130 г", false),
            RecipeIngredientUi(5, "Яйцо", "1 шт.", false)
        )

        ingredientAdapter = RecipeIngredientAdapter(ingredients)

        binding.ingredientsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.ingredientsRecyclerView.adapter = ingredientAdapter

        val missingCount = ingredients.count { !it.isAvailable }
        binding.missingTextView.text =
            getString(R.string.recipe_missing_ingredients, missingCount)

        updatePortionsUi()

        binding.increasePortionButton.setOnClickListener {
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

    private fun updatePortionsUi() {
        binding.portionCountTextView.text = portionCount.toString()
        ingredientAdapter.updatePortionCount(portionCount)
    }

    private fun setupClicks() {
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
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.addToShoppingListButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Недостающие ингредиенты добавим в список покупок позже",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateFavoriteUi() {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_heart_filled
            else R.drawable.ic_heart
        )
    }

    private fun setupSteps() {

        val steps = mutableListOf(
            RecipeStepUi(
                id = 1,
                stepNumber = 1,
                text = "Натираем сыр на мелкой терке. Так блюдо получится вкуснее.",
                isExpanded = true // первый открыт
            ),
            RecipeStepUi(
                id = 2,
                stepNumber = 2,
                text = "Ставим воду для пасты. Пока она закипает, нарезаем бекон и слегка обжариваем его.",
                isExpanded = false
            ),
            RecipeStepUi(
                id = 3,
                stepNumber = 3,
                text = "Отвариваем пасту до состояния аль денте и сохраняем немного воды от варки.",
                isExpanded = false
            ),
            RecipeStepUi(
                id = 4,
                stepNumber = 4,
                text = "Смешиваем пасту с соусом, сыром и беконом. Добавляем немного воды от пасты.",
                isExpanded = false
            ),
            RecipeStepUi(
                id = 5,
                stepNumber = 5,
                text = "Добавляем сыр и быстро перемешиваем до кремовой текстуры. Подаем сразу.",
                isExpanded = false
            )
        )

        val adapter = RecipeStepAdapter(steps)

        binding.stepsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.stepsRecyclerView.adapter = adapter
    }

    private fun setupRatingClicks() {
        binding.ratingBlock.badRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Нажата плохая оценка", Toast.LENGTH_SHORT).show()
        }

        binding.ratingBlock.neutralRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Нажата нейтральная оценка", Toast.LENGTH_SHORT).show()
        }

        binding.ratingBlock.goodRatingButton.setOnClickListener {
            Toast.makeText(requireContext(), "Нажата хорошая оценка", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}