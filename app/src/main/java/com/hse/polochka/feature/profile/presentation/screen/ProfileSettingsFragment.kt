package com.hse.polochka.feature.profile.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hse.polochka.R
import com.hse.polochka.core.family.FamilyMember
import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.preferences.PreferencesStorage
import com.hse.polochka.databinding.ActivityProfileBinding
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.provider.PreferenceChipProvider
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment

class ProfileSettingsFragment : Fragment(R.layout.activity_profile) {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var likedPreferencesAdapter: PreferenceChipAdapter
    private lateinit var restrictedPreferencesAdapter: PreferenceChipAdapter
    private lateinit var preferencesStorage: PreferencesStorage
    private lateinit var familyStorage: FamilyStorage
    private var preferencesExpanded = false
    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityProfileBinding.bind(view)
        preferencesStorage = PreferencesStorage(requireContext())
        familyStorage = FamilyStorage(requireContext())

        bindProfile()
        setupPreferences()
        bindFamily()
        setupClicks()
    }

    private fun bindProfile() {
        val currentUser = familyStorage.getMembers().first { it.isCurrentUser }
        binding.nameTextView.text = currentUser.name
        binding.emailTextView.text = currentUser.email
        binding.nameEditText.setText(currentUser.name)
        binding.emailEditText.setText(currentUser.email)
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
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            binding.nameTextView.visibility = View.GONE
            binding.emailTextView.visibility = View.GONE
            binding.nameEditText.visibility = View.VISIBLE
            binding.emailEditText.visibility = View.VISIBLE
        } else {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            familyStorage.updateCurrentUser(name = name, email = email)
            bindProfile()
            bindFamily()

            binding.nameTextView.visibility = View.VISIBLE
            binding.emailTextView.visibility = View.VISIBLE
            binding.nameEditText.visibility = View.GONE
            binding.emailEditText.visibility = View.GONE
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
        binding.familyTitleTextView.text = familyStorage.getFamilyName().uppercase()
        binding.familyMembersContainer.removeAllViews()
        familyStorage.getMembers().forEach(::addFamilyMemberView)
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
}
