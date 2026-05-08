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

class OnboardingLikedProductsFragment :
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
            step = 2,
            stepViews = listOf(
                binding.step1,
                binding.step2,
                binding.step3,
                binding.step4
            )
        )

        binding.titleTextView.setText(R.string.onboarding_liked_title)

        chipAdapter = PreferenceChipAdapter(
            PreferenceChipProvider.getProductChips(
                preferencesStorage.getState().likedTagIds
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
            saveLikedProducts(chipAdapter.getSelectedIds())
            openFragment(OnboardingDislikedProductsFragment())
        }

        binding.skipButton.setOnClickListener {
            saveLikedProducts(emptyList())
            openFragment(OnboardingDislikedProductsFragment())
        }
    }

    private fun saveLikedProducts(tagIds: List<String>) {
        viewModel.saveLikedProducts(tagIds)
        preferencesStorage.saveLikedTagIds(tagIds)
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
