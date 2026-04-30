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

class OnboardingCategoriesFragment : Fragment(R.layout.activity_onboarding_chip_selection) {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private var _binding: ActivityOnboardingChipSelectionBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var chipAdapter: PreferenceChipAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityOnboardingChipSelectionBinding.bind(view)

        binding.progressTextView.setText(R.string.onboarding_progress_1)
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
            val selectedCategoryIds = chipAdapter.getSelectedIds()
            viewModel.saveSelectedCategories(selectedCategoryIds)

            // дальше будет переход на экран любимых продуктов
        }

        binding.skipButton.setOnClickListener {
            // позже переход на следующий шаг или Home
        }
    }

    private fun getCategoryChips(): MutableList<PreferenceChipUi> {
        return mutableListOf(
            PreferenceChipUi(1, R.string.pref_milk),
            PreferenceChipUi(2, R.string.pref_sour_milk),
            PreferenceChipUi(3, R.string.pref_cheese),
            PreferenceChipUi(4, R.string.pref_meat),
            PreferenceChipUi(5, R.string.pref_fish),
            PreferenceChipUi(6, R.string.pref_poultry),
            PreferenceChipUi(7, R.string.pref_seafood),
            PreferenceChipUi(8, R.string.pref_vegetables),
            PreferenceChipUi(9, R.string.pref_fruits),
            PreferenceChipUi(10, R.string.pref_grains),
            PreferenceChipUi(11, R.string.pref_bread),
            PreferenceChipUi(12, R.string.pref_sweets),
            PreferenceChipUi(13, R.string.pref_drinks)
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}