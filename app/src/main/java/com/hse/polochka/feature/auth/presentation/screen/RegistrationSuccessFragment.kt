package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityRegistrationSuccessBinding

class RegistrationSuccessFragment : Fragment(R.layout.activity_registration_success) {

    private var _binding: ActivityRegistrationSuccessBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityRegistrationSuccessBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.startSetupButton.setOnClickListener {
            // дальше откроем экран выбора категорий
        }

        binding.skipSetupButton.setOnClickListener {
            // позже откроем HomeFragment
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}