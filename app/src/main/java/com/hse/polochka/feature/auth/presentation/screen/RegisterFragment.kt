package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityAuthRegisterBinding

class RegisterFragment : Fragment(R.layout.activity_auth_register) {

    private var _binding: ActivityAuthRegisterBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityAuthRegisterBinding.bind(view)

        binding.goLoginTextView.setOnClickListener {
            openFragment(LoginFragment())
        }

        binding.registerSubmitButton.setOnClickListener {
            openFragment(RegistrationSuccessFragment())
        }
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