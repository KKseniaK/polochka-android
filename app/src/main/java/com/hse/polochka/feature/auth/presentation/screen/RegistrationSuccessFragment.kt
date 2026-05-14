package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hse.polochka.MainActivity
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityRegistrationSuccessBinding
import com.hse.polochka.feature.onboarding.presentation.screen.OnboardingCategoriesFragment

class RegistrationSuccessFragment : Fragment(R.layout.activity_registration_success) {

    private var _binding: ActivityRegistrationSuccessBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityRegistrationSuccessBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).hideBottomMenu()

        binding.startSetupButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, OnboardingCategoriesFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.skipSetupButton.setOnClickListener {
            PreferencesStorage(requireContext()).markOnboardingCompleted()
            (requireActivity() as MainActivity).openHome()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
