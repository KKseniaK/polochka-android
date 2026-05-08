package com.hse.polochka.feature.onboarding.presentation.screen

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hse.polochka.MainActivity
import com.hse.polochka.R
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityOnboardingFamilyBinding
import com.hse.polochka.feature.onboarding.presentation.viewmodel.OnboardingViewModel

class OnboardingFamilyFragment : BaseOnboardingFragment(R.layout.activity_onboarding_family) {

    private var _binding: ActivityOnboardingFamilyBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var preferencesStorage: PreferencesStorage

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                binding.avatarImageView.setImageURI(uri)
                binding.avatarImageView.setPadding(0, 0, 0, 0)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityOnboardingFamilyBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())

        setupProgress(
            step = 4,
            stepViews = listOf(
                binding.step1,
                binding.step2,
                binding.step3,
                binding.step4
            )
        )

        binding.avatarImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.addMemberButton.setOnClickListener {
            InviteMemberDialogFragment().show(
                parentFragmentManager,
                "InviteMemberDialog"
            )
        }

        binding.doneButton.setOnClickListener {

            val userName = viewModel.userName.value
                ?: getString(R.string.default_user_name) // временная заглушка

            viewModel.saveFamilySettings(
                userName = userName,
                avatarUri = selectedImageUri?.toString()
            )

            Toast.makeText(
                requireContext(),
                getString(R.string.toast_onboarding_saved),
                Toast.LENGTH_SHORT
            ).show()

            preferencesStorage.markOnboardingCompleted()
            openHome()
        }

        binding.skipButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_onboarding_skipped),
                Toast.LENGTH_SHORT
            ).show()

            preferencesStorage.markOnboardingCompleted()
            openHome()
        }

    }

    private fun openHome() {
        (requireActivity() as MainActivity).openHome()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
