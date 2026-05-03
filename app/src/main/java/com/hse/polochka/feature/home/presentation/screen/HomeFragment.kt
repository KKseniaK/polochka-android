package com.hse.polochka.feature.home.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityHomeBinding
import com.hse.polochka.feature.profile.presentation.screen.ProfileSettingsFragment

class HomeFragment : Fragment(R.layout.activity_home) {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ActivityHomeBinding.bind(view)

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileSettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}