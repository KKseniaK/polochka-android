package com.hse.polochka.feature.profile.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityProfileBinding
import com.hse.polochka.feature.onboarding.presentation.adapter.PreferenceChipAdapter
import com.hse.polochka.feature.onboarding.presentation.provider.PreferenceChipProvider
import com.hse.polochka.feature.onboarding.presentation.screen.InviteMemberDialogFragment

class ProfileSettingsFragment : Fragment(R.layout.activity_profile) {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var preferencesAdapter: PreferenceChipAdapter
    private var preferencesExpanded = false

    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityProfileBinding.bind(view)

        setMockProfile()
        setupPreferences()
        setMockFamily()
        setupClicks()
    }

    private fun setMockProfile() {
        binding.nameTextView.text = "Вася Пупкин"
        binding.emailTextView.text = "vasyapupkin@gmail.com"
        binding.nameEditText.setText(binding.nameTextView.text)
        binding.emailEditText.setText(binding.emailTextView.text)
    }

    private fun setupClicks() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.editProfileButton.setOnClickListener {
            toggleEditMode()
        }

        binding.addFamilyMemberButton.setOnClickListener {
            InviteMemberDialogFragment()
                .show(parentFragmentManager, "invite_member")
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
            binding.nameTextView.text = binding.nameEditText.text.toString()
            binding.emailTextView.text = binding.emailEditText.text.toString()

            binding.nameTextView.visibility = View.VISIBLE
            binding.emailTextView.visibility = View.VISIBLE
            binding.nameEditText.visibility = View.GONE
            binding.emailEditText.visibility = View.GONE
        }
    }

    private fun setupPreferences() {
        val selectedIds = listOf(112, 110, 121, 104, 106, 103)

        preferencesAdapter = PreferenceChipAdapter(
            PreferenceChipProvider.getProductChips(selectedIds)
        )

        binding.preferencesRecyclerView.layoutManager =
            FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }

        binding.preferencesRecyclerView.adapter = preferencesAdapter

        binding.preferencesExpandButton.setOnClickListener {
            preferencesExpanded = !preferencesExpanded
            binding.preferencesRecyclerView.visibility =
                if (preferencesExpanded) View.VISIBLE else View.GONE
        }
    }

    private fun setMockFamily() {
        val members = listOf(
            "Папа" to "papa******@gmail.com",
            "Сестра" to "sis******@gmail.com",
            "Мама" to "mom******@gmail.com"
        )

        members.forEach { (name, email) ->
            val item = layoutInflater.inflate(
                R.layout.item_family_member,
                binding.familyMembersContainer,
                false
            )

            item.findViewById<TextView>(R.id.memberNameTextView).text = name
            item.findViewById<TextView>(R.id.memberEmailTextView).text = email

            binding.familyMembersContainer.addView(item)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}