package com.hse.polochka.feature.profile.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hse.polochka.R
import com.hse.polochka.MainActivity
import com.hse.polochka.core.family.FamilyMember
import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.databinding.ActivityProfileBinding
import com.hse.polochka.feature.family.data.remote.FamilyApi
import com.hse.polochka.feature.family.data.repository.FamilyRepositoryImpl
import com.hse.polochka.feature.onboarding.data.remote.OnboardingApi
import com.hse.polochka.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.provider.PreferenceChipProvider
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment
import com.hse.polochka.feature.profile.data.remote.ProfileApi
import com.hse.polochka.feature.profile.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.launch

class ProfileSettingsFragment : Fragment(R.layout.activity_profile) {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var likedPreferencesAdapter: PreferenceChipAdapter
    private lateinit var restrictedPreferencesAdapter: PreferenceChipAdapter
    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var familyStorage: FamilyStorage
    private lateinit var profileRepository: ProfileRepositoryImpl
    private lateinit var familyRepository: FamilyRepositoryImpl
    private lateinit var onboardingRepository: OnboardingRepositoryImpl
    private var preferencesExpanded = false
    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityProfileBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        familyStorage = FamilyStorage(requireContext())
        profileRepository = createProfileRepository()
        familyRepository = createFamilyRepository()
        onboardingRepository = createOnboardingRepository()

        bindProfile()
        setupPreferences()
        bindFamily()
        setupClicks()
        loadRemoteProfile()
        loadRemoteFamily()
        loadRemotePreferences()
    }

    private fun bindProfile() {
        val currentUser = familyStorage.getMembers().first { it.isCurrentUser }
        bindProfileValues(currentUser.name, currentUser.email)
    }

    private fun bindProfileValues(name: String, email: String) {
        binding.nameTextView.text = name
        binding.emailTextView.text = email
        binding.nameEditText.setText(name)
        binding.emailEditText.setText(email)
    }

    private fun setupClicks() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.editProfileButton.setOnClickListener {
            toggleEditMode()
        }

        binding.addFamilyMemberButton.setOnClickListener {
            InviteMemberDialogFragment().apply {
                onInvitationCreated = {
                    bindFamily()
                }
            }.show(parentFragmentManager, "invite_member")
        }

        binding.logoutButton.setOnClickListener {
            (requireActivity() as MainActivity).logoutToWelcome()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            binding.nameTextView.visibility = View.GONE
            binding.emailTextView.visibility = View.GONE
            binding.nameEditText.visibility = View.VISIBLE
            binding.emailEditText.visibility = View.VISIBLE
        } else {
            saveProfileChanges()
        }
    }

    private fun saveProfileChanges() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()

        when {
            name.isBlank() -> {
                binding.nameEditText.error = getString(R.string.auth_error_enter_name)
                isEditMode = true
                return
            }
            !email.isValidEmail() -> {
                binding.emailEditText.error = getString(R.string.auth_error_enter_email)
                isEditMode = true
                return
            }
        }

        binding.editProfileButton.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                profileRepository.updateProfile(displayName = name, email = email)
            }.onSuccess { profile ->
                bindProfileValues(profile.displayName, profile.email)
                loadRemoteFamily()
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    error.message ?: getString(R.string.auth_error_request_failed),
                    Toast.LENGTH_SHORT
                ).show()
                bindProfile()
            }

            binding.nameTextView.visibility = View.VISIBLE
            binding.emailTextView.visibility = View.VISIBLE
            binding.nameEditText.visibility = View.GONE
            binding.emailEditText.visibility = View.GONE
            binding.editProfileButton.isEnabled = true
            isEditMode = false
        }
    }

    private fun setupPreferences() {
        val preferences = preferencesStorage.getState()
        likedPreferencesAdapter = PreferenceChipAdapter(
            PreferenceChipProvider.getProductChips(preferences.likedTagIds),
        ) { selectedIds ->
            preferencesStorage.saveLikedTagIds(selectedIds)
        }

        restrictedPreferencesAdapter = PreferenceChipAdapter(
            (
                PreferenceChipProvider.getRestrictionChips(preferences.restrictedTagIds) +
                    PreferenceChipProvider.getProductChips(preferences.restrictedTagIds)
                ).toMutableList(),
        ) { selectedIds ->
            preferencesStorage.saveRestrictedTagIds(selectedIds)
        }

        binding.likedPreferencesRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }

        binding.restrictedPreferencesRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }

        binding.likedPreferencesRecyclerView.adapter = likedPreferencesAdapter
        binding.restrictedPreferencesRecyclerView.adapter = restrictedPreferencesAdapter

        binding.preferencesExpandButton.setOnClickListener {
            preferencesExpanded = !preferencesExpanded
            updatePreferencesVisibility()
        }
    }

    private fun updatePreferencesVisibility() {
        val visibility = if (preferencesExpanded) View.VISIBLE else View.GONE
        binding.likedPreferencesTitleTextView.visibility = visibility
        binding.likedPreferencesRecyclerView.visibility = visibility
        binding.restrictedPreferencesTitleTextView.visibility = visibility
        binding.restrictedPreferencesRecyclerView.visibility = visibility
    }

    private fun bindFamily() {
        bindFamily(familyStorage.getFamilyName(), familyStorage.getMembers())
    }

    private fun bindFamily(familyName: String, members: List<FamilyMember>) {
        binding.familyTitleTextView.text = familyName.uppercase()
        binding.familyMembersContainer.removeAllViews()
        members.forEach(::addFamilyMemberView)
    }

    private fun addFamilyMemberView(member: FamilyMember) {
        val item = layoutInflater.inflate(
            R.layout.item_family_member,
            binding.familyMembersContainer,
            false
        )

        item.findViewById<TextView>(R.id.memberNameTextView).text = member.name
        item.findViewById<TextView>(R.id.memberEmailTextView).text = member.email
        item.findViewById<TextView>(R.id.memberRoleTextView).text = getMemberRoleText(member)
        item.findViewById<View>(R.id.removeMemberButton).setOnClickListener {
            if (familyStorage.removeMember(member.id)) {
                Toast.makeText(requireContext(), getString(R.string.profile_member_removed), Toast.LENGTH_SHORT).show()
                bindFamily()
            } else {
                Toast.makeText(requireContext(), getString(R.string.profile_cannot_remove_self), Toast.LENGTH_SHORT).show()
            }
        }

        binding.familyMembersContainer.addView(item)
    }

    private fun loadRemoteProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                profileRepository.getProfile()
            }.onSuccess { profile ->
                bindProfileValues(profile.displayName, profile.email)
            }
        }
    }

    private fun loadRemoteFamily() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                familyRepository.getFamilyName() to familyRepository.getMembers()
            }.onSuccess { (familyName, members) ->
                bindFamily(familyName, members)
            }
        }
    }

    private fun loadRemotePreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                onboardingRepository.getOnboardingState()
            }.onSuccess { state ->
                preferencesStorage.saveLikedTagIds(state.likedTagIds)
                preferencesStorage.saveRestrictedTagIds(state.restrictedTagIds)
                setupPreferences()
                updatePreferencesVisibility()
            }
        }
    }

    private fun createProfileRepository(): ProfileRepositoryImpl =
        ProfileRepositoryImpl(
            profileApi = ApiClient.create(ProfileApi::class.java),
            familyStorage = familyStorage,
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

    private fun createFamilyRepository(): FamilyRepositoryImpl =
        FamilyRepositoryImpl(
            familyApi = ApiClient.create(FamilyApi::class.java),
            familyStorage = familyStorage,
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

    private fun createOnboardingRepository(): OnboardingRepositoryImpl =
        OnboardingRepositoryImpl(
            onboardingApi = ApiClient.create(OnboardingApi::class.java),
            preferencesStorage = preferencesStorage,
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

    private fun getMemberRoleText(member: FamilyMember): String {
        if (member.status == "invited") {
            return getString(R.string.profile_family_invited)
        }
        return if (member.role == "owner") {
            getString(R.string.profile_family_owner)
        } else {
            getString(R.string.profile_family_member)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun String.isValidEmail(): Boolean =
        contains("@") && substringAfter("@").contains(".") && !contains(" ")
}
