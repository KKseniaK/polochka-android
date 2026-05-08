package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hse.polochka.MainActivity
import com.hse.polochka.R
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.databinding.ActivityAuthLoginBinding
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.auth.data.repository.AuthRepositoryImpl
import com.hse.polochka.feature.auth.presentation.state.AuthUiState
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModel
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.activity_auth_login) {

    private var _binding: ActivityAuthLoginBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityAuthLoginBinding.bind(view)
        viewModel = createViewModel()

        binding.forgotPasswordTextView.setOnClickListener {
            openFragment(ForgotPasswordFragment())
        }

        binding.goRegisterTextView.setOnClickListener {
            openFragment(RegisterFragment())
        }

        binding.loginSubmitButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isBlank() -> showMessage("Enter email")
                password.isBlank() -> showMessage("Enter password")
                else -> viewModel.login(email, password)
            }
        }

        binding.googleRegisterButton.setOnClickListener {
            showMessage("Google login will be added later")
        }

        binding.vkRegisterButton.setOnClickListener {
            showMessage("VK login will be added later")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.loginSubmitButton.isEnabled = state !is AuthUiState.Loading
                when (state) {
                    is AuthUiState.Authenticated -> {
                        viewModel.resetState()
                        (requireActivity() as MainActivity).openHome()
                    }
                    is AuthUiState.Error -> {
                        showMessage(state.message)
                        viewModel.resetState()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createViewModel(): AuthViewModel {
        val repository = AuthRepositoryImpl(
            authApi = ApiClient.create(AuthApi::class.java),
            sessionStorage = UserSessionStorage(requireContext()),
        )
        return ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
