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
import com.hse.polochka.databinding.ActivityOnboardingChipSelectionBinding
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi
import com.hse.polochka.feature.onboarding.presentation.viewmodel.OnboardingViewModel

class OnboardingDislikedProductsFragment :
    BaseOnboardingFragment(R.layout.activity_onboarding_chip_selection) {

    private var _binding: ActivityOnboardingChipSelectionBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var chipAdapter: PreferenceChipAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityOnboardingChipSelectionBinding.bind(view)

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

        chipAdapter = PreferenceChipAdapter(getProductChips())

        binding.chipsRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }

        binding.chipsRecyclerView.adapter = chipAdapter

        binding.nextButton.setOnClickListener {
            viewModel.saveDislikedProducts(chipAdapter.getSelectedIds())
            openFragment(OnboardingFamilyFragment())
        }

        binding.skipButton.setOnClickListener {
            openFragment(OnboardingFamilyFragment())
        }
    }

    private fun getProductChips(): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi(101, R.string.pref_milk, R.drawable.ic_milk),
            PreferenceChipUi(102, R.string.pref_sour_milk, R.drawable.ic_sour_milk),
            PreferenceChipUi(103, R.string.pref_cheese, R.drawable.ic_cheese),
            PreferenceChipUi(104, R.string.pref_meat, R.drawable.ic_meat),
            PreferenceChipUi(105, R.string.pref_fish, R.drawable.ic_fish),
            PreferenceChipUi(106, R.string.pref_poultry, R.drawable.ic_poultry),
            PreferenceChipUi(107, R.string.pref_seafood, R.drawable.ic_seafood),
            PreferenceChipUi(108, R.string.pref_sausage, R.drawable.ic_sausage),
            PreferenceChipUi(109, R.string.pref_vegetables, R.drawable.ic_vegetables),
            PreferenceChipUi(110, R.string.pref_fruits, R.drawable.ic_fruits),
            PreferenceChipUi(111, R.string.pref_greens, R.drawable.ic_greens),
            PreferenceChipUi(112, R.string.pref_berries, R.drawable.ic_berries),
            PreferenceChipUi(113, R.string.pref_mushrooms, R.drawable.ic_mushrooms),
            PreferenceChipUi(114, R.string.pref_bakery, R.drawable.ic_bakery),
            PreferenceChipUi(115, R.string.pref_pasta, R.drawable.ic_pasta),
            PreferenceChipUi(116, R.string.pref_grains, R.drawable.ic_grains),
            PreferenceChipUi(117, R.string.pref_coffee, R.drawable.ic_coffee),
            PreferenceChipUi(118, R.string.pref_tea, R.drawable.ic_tea),
            PreferenceChipUi(119, R.string.pref_sweet, R.drawable.ic_sweet),
            PreferenceChipUi(120, R.string.pref_salty, R.drawable.ic_salty),
            PreferenceChipUi(121, R.string.pref_hot, R.drawable.ic_hot)
        )
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