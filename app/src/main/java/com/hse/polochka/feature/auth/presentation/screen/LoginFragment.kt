package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hse.polochka.R
import com.hse.polochka.databinding.ActivityAuthLoginBinding

class LoginFragment : Fragment(R.layout.activity_auth_login) {

    private var _binding: ActivityAuthLoginBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityAuthLoginBinding.bind(view)

        binding.forgotPasswordTextView.setOnClickListener {
            openFragment(ForgotPasswordFragment())
        }

        binding.goRegisterTextView.setOnClickListener {
            openFragment(RegisterFragment())
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