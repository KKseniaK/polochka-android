package com.hse.polochka.feature.onboarding.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityOnboardingChipSelectionBinding
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.provider.PreferenceChipProvider
import com.hse.polochka.feature.onboarding.presentation.viewmodel.OnboardingViewModel

class OnboardingDislikedProductsFragment :
    BaseOnboardingFragment(R.layout.activity_onboarding_chip_selection) {

    private var _binding: ActivityOnboardingChipSelectionBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var chipAdapter: PreferenceChipAdapter
    private lateinit var preferencesStorage: PreferencesStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityOnboardingChipSelectionBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())

        setupProgress(
            step = 3,
            stepViews = listOf(
                binding.step1,
                binding.step2,
                binding.step3,
                binding.step4
            )
        )

        binding.titleTextView.setText(R.string.onboarding_disliked_title)

        chipAdapter = PreferenceChipAdapter(
            PreferenceChipProvider.getProductChips(
                preferencesStorage.getState().restrictedTagIds
            )
        )

        binding.chipsRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }

        binding.chipsRecyclerView.adapter = chipAdapter

        binding.nextButton.setOnClickListener {
            saveDislikedProducts(chipAdapter.getSelectedIds())
            openFragment(OnboardingFamilyFragment())
        }

        binding.skipButton.setOnClickListener {
            saveDislikedProducts(emptyList())
            openFragment(OnboardingFamilyFragment())
        }
    }

    private fun saveDislikedProducts(tagIds: List<String>) {
        val allRestrictedTagIds = (viewModel.uiState.selectedCategoryIds + tagIds).distinct()
        viewModel.saveDislikedProducts(tagIds)
        preferencesStorage.saveRestrictedTagIds(allRestrictedTagIds)
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
