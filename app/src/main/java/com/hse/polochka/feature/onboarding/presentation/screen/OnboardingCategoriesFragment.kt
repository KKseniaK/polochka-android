package com.hse.polochka.feature.onboarding.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityOnboardingChipSelectionBinding
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi
import androidx.fragment.app.activityViewModels
import com.hse.polochka.feature.onboarding.presentation.viewmodel.OnboardingViewModel

class OnboardingCategoriesFragment : BaseOnboardingFragment(R.layout.activity_onboarding_chip_selection) {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private var _binding: ActivityOnboardingChipSelectionBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var chipAdapter: PreferenceChipAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityOnboardingChipSelectionBinding.bind(view)

        setupProgress(
            step = 1,
            stepViews = listOf(
                binding.step1,
                binding.step2,
                binding.step3,
                binding.step4
            )
        )

        binding.titleTextView.setText(R.string.onboarding_categories_title)

        chipAdapter = PreferenceChipAdapter(getCategoryChips())

        binding.chipsRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }

        binding.chipsRecyclerView.adapter = chipAdapter

        binding.nextButton.setOnClickListener {
            viewModel.saveSelectedCategories(chipAdapter.getSelectedIds())
            openFragment(OnboardingLikedProductsFragment())
        }

        binding.skipButton.setOnClickListener {
            openFragment(OnboardingLikedProductsFragment())
        }
    }

    private fun getCategoryChips(): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi(
                id = 1,
                titleResId = R.string.pref_gluten_free,
                iconResId = R.drawable.ic_glutenfree
            ),
            PreferenceChipUi(
                id = 2,
                titleResId = R.string.pref_lactose_free,
                iconResId = R.drawable.ic_lactosefree
            ),
            PreferenceChipUi(
                id = 3,
                titleResId = R.string.pref_vegan,
                iconResId = R.drawable.ic_vegan
            ),
            PreferenceChipUi(
                id = 4,
                titleResId = R.string.pref_vegetarian,
                iconResId = R.drawable.ic_vegetarian
            ),
            PreferenceChipUi(
                id = 5,
                titleResId = R.string.pref_protein,
                iconResId = R.drawable.ic_protein
            ),
            PreferenceChipUi(
                id = 6,
                titleResId = R.string.pref_diet,
            ),
            PreferenceChipUi(
                id = 7,
                titleResId = R.string.pref_fatty,
            ),
            PreferenceChipUi(
                id = 8,
                titleResId = R.string.pref_spicy,
            ),
            PreferenceChipUi(
                id = 9,
                titleResId = R.string.pref_sugar_free,
            ),
            PreferenceChipUi(
                id = 10,
                titleResId = R.string.pref_halal,
                iconResId = R.drawable.ic_halal
            )
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