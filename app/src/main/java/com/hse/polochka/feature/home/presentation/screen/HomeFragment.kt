package com.hse.polochka.feature.home.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.core.storage_events.StorageEventStorage
import com.hse.polochka.databinding.ActivityHomeBinding
import com.hse.polochka.feature.analytics.data.remote.AnalyticsApi
import com.hse.polochka.feature.analytics.data.repository.AnalyticsRepositoryImpl
import com.hse.polochka.feature.family.data.remote.FamilyApi
import com.hse.polochka.feature.family.data.repository.FamilyRepositoryImpl
import com.hse.polochka.feature.home.presentation.adapter.HomeExpiringProductsAdapter
import com.hse.polochka.feature.home.presentation.adapter.HomeFamilyAdapter
import com.hse.polochka.feature.home.presentation.model.HomeExpiringProductUi
import com.hse.polochka.feature.home.presentation.model.HomeFamilyMemberUi
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment
import com.hse.polochka.feature.profile.presentation.screen.ProfileSettingsFragment
import com.hse.polochka.feature.recipes.data.RecipeRepository
import com.hse.polochka.feature.recipes.presentation.adapter.RecipeAdapter
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi
import com.hse.polochka.feature.recipes.presentation.screen.RecipeDetailsFragment
import com.hse.polochka.feature.recipes.presentation.screen.RecipesFragment
import com.hse.polochka.feature.storage.data.remote.StorageApi
import com.hse.polochka.feature.storage.data.repository.StorageRepositoryImpl
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi
import com.hse.polochka.feature.storage.presentation.screen.StorageFragment
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.activity_home) {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var familyStorage: FamilyStorage
    private lateinit var familyRepository: FamilyRepositoryImpl
    private lateinit var storageRepository: StorageRepositoryImpl
    private lateinit var analyticsRepository: AnalyticsRepositoryImpl
    private lateinit var recipeRepository: RecipeRepository
    private var hasShownExpirationAlert = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as com.hse.polochka.MainActivity).showBottomMenu()

        _binding = ActivityHomeBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        familyStorage = FamilyStorage(requireContext())

        val authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext()))
        familyRepository = createFamilyRepository(authHeaderProvider)
        storageRepository = createStorageRepository(authHeaderProvider)
        analyticsRepository = createAnalyticsRepository(authHeaderProvider)
        recipeRepository = RecipeRepository(requireContext())

        setupClicks()
        hideFamilyStrip()
        setupFamilyBlock()
        setupExpiringProductsBlock()
        setupStatsBlock()
        setupRecipeBlock()

        loadRemoteFamilyBlock()
        loadExpiringProducts()
        loadStats()
        loadRecipes()
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
        bindFamilyMembers(familyStorage.getMembers())
    }

    private fun hideFamilyStrip() {
        binding.familyRecyclerView.isVisible = false
    }

    private fun bindFamilyMembers(members: List<com.hse.polochka.core.family.FamilyMember>) {
        binding.familyRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.familyRecyclerView.adapter =
            HomeFamilyAdapter(
                items = members.mapIndexed { index, member ->
                    HomeFamilyMemberUi(
                        id = index + 1,
                        name = member.name,
                        message = if (member.status == "invited") "ожидает приглашение" else null,
                        avatarResId = R.drawable.ic_profile_placeholder,
                    )
                },
                onAddClick = {
                    InviteMemberDialogFragment().apply {
                        onInvitationCreated = {
                            loadRemoteFamilyBlock()
                        }
                    }.show(parentFragmentManager, "invite_member")
                },
            )
    }

    private fun loadRemoteFamilyBlock() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                familyRepository.getMembers()
            }.onSuccess(::bindFamilyMembers)
        }
    }

    private fun setupExpiringProductsBlock() {
        binding.expiringProductsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.expiringProductsRecyclerView.adapter = HomeExpiringProductsAdapter(emptyList())
        renderExpiringProducts(emptyList())
    }

    private fun loadExpiringProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                storageRepository.getProducts()
                    .filterNot { it.isWrittenOff }
                    .filter { it.expirationAtMillis != null }
                    .sortedBy { it.expirationAtMillis }
            }.onSuccess { products ->
                val soonProducts = products.take(4)
                renderExpiringProducts(soonProducts.map { it.toHomeExpiringProduct() }, products.size)
                showExpirationAlertIfNeeded(products)
            }.onFailure {
                renderExpiringProducts(emptyList())
            }
        }
    }

    private fun renderExpiringProducts(products: List<HomeExpiringProductUi>, totalCount: Int = products.size) {
        binding.expiringTitleTextView.text = if (totalCount == 0) {
            getString(R.string.home_expiring_empty_title)
        } else {
            "Скоро пропадут\n$totalCount продуктов"
        }
        binding.expiringProductsRecyclerView.isVisible = products.isNotEmpty()
        binding.expiringEmptyTextView.isVisible = products.isEmpty()
        binding.expiringProductsRecyclerView.adapter = HomeExpiringProductsAdapter(products)
    }

    private fun setupStatsBlock() {
        binding.monthStatsBlock.savedProductsValueTextView.text = "0%"
        binding.monthStatsBlock.savedMoneyValueTextView.text = "0 руб"
        binding.monthStatsBlock.monthStatsEmptyTextView.isVisible = true
    }

    private fun loadStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                analyticsRepository.getMonthlySummary(currentMonth())
            }.onSuccess { summary ->
                val savedPercent = summary.percentChart.firstOrNull { it.key == "saved" }?.value ?: 0
                binding.monthStatsBlock.savedProductsValueTextView.text = "$savedPercent%"
                binding.monthStatsBlock.savedMoneyValueTextView.text = "${summary.savedMoneyRub} руб"
                binding.monthStatsBlock.monthStatsEmptyTextView.isVisible =
                    savedPercent == 0 && summary.savedMoneyRub == 0
            }
        }
    }

    private fun setupRecipeBlock() {
        binding.homeRecipesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeRecipesRecyclerView.adapter = RecipeAdapter(emptyList<RecipeUi>()) { recipe ->
            openRecipe(recipe)
        }
        renderHomeRecipes(emptyList())

        binding.homeShowAllRecipesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecipesFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadRecipes() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                recipeRepository.getRecipes(preferencesStorage.getState()).filter { it.canCook }.take(3)
            }.onSuccess { recipes ->
                renderHomeRecipes(recipes)
            }
        }
    }

    private fun renderHomeRecipes(recipes: List<RecipeUi>) {
        binding.homeRecipesRecyclerView.isVisible = recipes.isNotEmpty()
        binding.homeRecipesEmptyTextView.isVisible = recipes.isEmpty()
        binding.homeRecipesRecyclerView.adapter = RecipeAdapter(recipes) { recipe ->
            openRecipe(recipe)
        }
    }

    private fun openRecipe(recipe: RecipeUi) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeDetailsFragment.newInstance(recipe.id))
            .addToBackStack(null)
            .commit()
    }

    private fun createFamilyRepository(authHeaderProvider: AuthHeaderProvider): FamilyRepositoryImpl =
        FamilyRepositoryImpl(
            familyApi = ApiClient.create(FamilyApi::class.java),
            familyStorage = familyStorage,
            authHeaderProvider = authHeaderProvider,
        )

    private fun createStorageRepository(authHeaderProvider: AuthHeaderProvider): StorageRepositoryImpl =
        StorageRepositoryImpl(
            storageApi = ApiClient.create(StorageApi::class.java),
            eventStorage = StorageEventStorage(requireContext()),
            authHeaderProvider = authHeaderProvider,
        )

    private fun createAnalyticsRepository(authHeaderProvider: AuthHeaderProvider): AnalyticsRepositoryImpl =
        AnalyticsRepositoryImpl(
            analyticsApi = ApiClient.create(AnalyticsApi::class.java),
            eventStorage = StorageEventStorage(requireContext()),
            authHeaderProvider = authHeaderProvider,
        )

    private fun StorageProductUi.toHomeExpiringProduct(): HomeExpiringProductUi =
        HomeExpiringProductUi(
            id = id,
            name = name,
            daysText = daysLeftText,
            iconResId = imageResId,
        )

    private fun showExpirationAlertIfNeeded(products: List<StorageProductUi>) {
        if (hasShownExpirationAlert || !isAdded || parentFragmentManager.isStateSaved) return

        val expired = products.filter { it.daysLeft < 0 }.take(ALERT_PRODUCTS_LIMIT)
        val today = products.filter { it.daysLeft == 0L }.take(ALERT_PRODUCTS_LIMIT)
        val soon = products.filter { it.daysLeft in 1L..2L }.take(ALERT_PRODUCTS_LIMIT)
        if (expired.isEmpty() && today.isEmpty() && soon.isEmpty()) return

        hasShownExpirationAlert = true
        ExpirationAlertDialogFragment
            .newInstance(buildExpirationAlertMessage(expired, today, soon))
            .apply {
                onOpenStorageClick = {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, StorageFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
            .show(parentFragmentManager, "expiration_alert")
    }

    private fun buildExpirationAlertMessage(
        expired: List<StorageProductUi>,
        today: List<StorageProductUi>,
        soon: List<StorageProductUi>,
    ): String =
        buildString {
            append("Некоторые продукты требуют внимания.\n")
            append("Проверьте хранилище, чтобы ничего не пропало.")
            appendProductGroup("Уже просрочено", expired)
            appendProductGroup("Срок сегодня", today)
            appendProductGroup("Скоро испортятся", soon)
        }

    private fun StringBuilder.appendProductGroup(title: String, products: List<StorageProductUi>) {
        if (products.isEmpty()) return
        append("\n\n")
        append(title)
        append(": ")
        append(products.joinToString { it.name })
    }

    private fun currentMonth(): String {
        val calendar = Calendar.getInstance()
        return String.format(Locale.US, "%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ALERT_PRODUCTS_LIMIT = 3
    }
}
