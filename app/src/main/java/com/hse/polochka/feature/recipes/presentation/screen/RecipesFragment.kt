package com.hse.polochka.feature.recipes.presentation.screen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityRecipesBinding
import com.hse.polochka.feature.recipes.data.RecipeRepository
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagFormatter
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeTagAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi
import java.util.Locale
import kotlinx.coroutines.launch

class RecipesFragment : Fragment(R.layout.activity_recipes) {

    private var _binding: ActivityRecipesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var recipeRepository: RecipeRepository
    private var allRecipes: List<RecipeUi> = emptyList()
    private var searchQuery: String = ""
    private var selectedTag: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityRecipesBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        recipeRepository = RecipeRepository(requireContext())

        setupLists()
        setupTags()
        setupSearch()
        setupClicks()
        loadRecipes()
    }

    private fun setupLists() {
        binding.canCookRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.popularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupTags() {
        binding.tagsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        renderTags()
    }

    private fun renderTags() {
        binding.tagsRecyclerView.adapter = RecipeTagAdapter(
            items = recipeRepository.getFilterTags(),
            selectedItem = selectedTag,
        ) { tag ->
            selectedTag = if (selectedTag == tag) null else tag
            renderTags()
            renderFilteredRecipes()
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                renderFilteredRecipes()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.searchEditText.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun loadRecipes() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                recipeRepository.getRecipes(preferencesStorage.getState())
            }.onSuccess { recipes ->
                allRecipes = recipes
                renderFilteredRecipes()
            }
                .onFailure {
                    allRecipes = emptyList()
                    renderFilteredRecipes()
                    Toast.makeText(requireContext(), "Не удалось загрузить рецепты", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun renderFilteredRecipes() {
        renderRecipes(allRecipes.filterBySearchAndTag())
    }

    private fun renderRecipes(recipes: List<RecipeUi>) {
        val canCookRecipes = recipes.filter { it.canCook }
        val hasStorageProducts = recipes.any { it.hasStorageProducts }
        val hasActiveFilter = searchQuery.isNotBlank() || selectedTag != null

        binding.canCookRecyclerView.isVisible = canCookRecipes.isNotEmpty()
        binding.canCookEmptyTextView.isVisible = canCookRecipes.isEmpty() && !hasActiveFilter
        binding.canCookEmptyTextView.text = when {
            hasStorageProducts -> "Пока нет рецептов, которые можно приготовить из продуктов в хранилище."
            else -> "У вас пока нет продуктов в хранилище. Сходите в магазин и сообщите Полочке, что вы купили, а мы посмотрим, что можно приготовить."
        }

        binding.canCookRecyclerView.adapter = RecipeAdapter(
            items = canCookRecipes,
            displayMode = RecipeAdapter.DisplayMode.Compact,
        ) { recipe ->
            openRecipeDetails(recipe.id)
        }

        // В этом блоке показываем все рецепты, которые вернул backend, без локального лимита.
        binding.popularRecyclerView.adapter = RecipeAdapter(
            items = recipes,
            displayMode = RecipeAdapter.DisplayMode.Wide,
        ) { recipe ->
            openRecipeDetails(recipe.id)
        }
        binding.popularRecyclerView.isVisible = recipes.isNotEmpty()
        binding.popularEmptyTextView.isVisible = recipes.isEmpty() && hasActiveFilter
    }

    private fun List<RecipeUi>.filterBySearchAndTag(): List<RecipeUi> {
        val normalizedQuery = searchQuery.normalizeForSearch()
        return filter { recipe ->
            val matchesSearch = normalizedQuery.isBlank() ||
                recipe.title.normalizeForSearch().contains(normalizedQuery) ||
                recipe.tagIds.any { tag ->
                    RecipeTagFormatter.readable(tag).normalizeForSearch().contains(normalizedQuery)
                }
            val matchesTag = selectedTag == null || selectedTag in recipe.tagIds
            matchesSearch && matchesTag
        }
    }

    private fun String.normalizeForSearch(): String =
        trim()
            .lowercase(Locale.getDefault())
            .replace('ё', 'е')

    private fun setupClicks() {
        binding.addRecipeButton.setOnClickListener {
            Toast.makeText(requireContext(), "Добавление рецепта позже", Toast.LENGTH_SHORT).show()
        }

        binding.favoritesButton.setOnClickListener {
            Toast.makeText(requireContext(), "Избранные рецепты позже", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openRecipeDetails(recipeId: Int) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeDetailsFragment.newInstance(recipeId))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
