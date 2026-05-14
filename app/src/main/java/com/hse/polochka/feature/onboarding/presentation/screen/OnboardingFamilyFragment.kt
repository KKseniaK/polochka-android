package com.hse.polochka.feature.onboarding.presentation.screen

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hse.polochka.MainActivity
import com.hse.polochka.R
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.core.preferences.TagPreferenceState
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.databinding.ActivityOnboardingFamilyBinding
import com.hse.polochka.feature.onboarding.data.remote.OnboardingApi
import com.hse.polochka.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.hse.polochka.feature.onboarding.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

class OnboardingFamilyFragment : BaseOnboardingFragment(R.layout.activity_onboarding_family) {

    private var _binding: ActivityOnboardingFamilyBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: OnboardingViewModel by activityViewModels()
    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var onboardingRepository: OnboardingRepositoryImpl

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
        onboardingRepository = createOnboardingRepository()

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

            completeOnboardingAndOpenHome(R.string.toast_onboarding_saved)
        }

        binding.skipButton.setOnClickListener {
            completeOnboardingAndOpenHome(R.string.toast_onboarding_skipped)
        }

    }

    private fun createOnboardingRepository(): OnboardingRepositoryImpl =
        OnboardingRepositoryImpl(
            onboardingApi = ApiClient.create(OnboardingApi::class.java),
            preferencesStorage = preferencesStorage,
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

    private fun completeOnboardingAndOpenHome(messageResId: Int) {
        val localState = preferencesStorage.getState()
        val completedState = TagPreferenceState(
            likedTagIds = localState.likedTagIds,
            restrictedTagIds = localState.restrictedTagIds,
            completedOnboarding = true,
        )

        binding.doneButton.isEnabled = false
        binding.skipButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                onboardingRepository.completeOnboarding(completedState)
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    error.message ?: getString(R.string.auth_error_request_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Toast.makeText(
                requireContext(),
                getString(messageResId),
                Toast.LENGTH_SHORT
            ).show()
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
