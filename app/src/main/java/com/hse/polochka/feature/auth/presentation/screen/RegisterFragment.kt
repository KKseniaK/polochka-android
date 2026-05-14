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
import com.hse.polochka.databinding.ActivityAuthRegisterBinding
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.auth.data.repository.AuthRepositoryImpl
import com.hse.polochka.feature.auth.presentation.state.AuthUiState
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModel
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.activity_auth_register) {

    private var _binding: ActivityAuthRegisterBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ActivityAuthRegisterBinding.bind(view)
        (requireActivity() as MainActivity).hideBottomMenu()
        viewModel = createViewModel()

        binding.goLoginTextView.setOnClickListener {
            openFragment(LoginFragment())
        }

        binding.registerSubmitButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()
            when {
                name.isBlank() -> showMessage(getString(R.string.auth_error_enter_name))
                email.isBlank() -> showMessage(getString(R.string.auth_error_enter_email))
                password.isBlank() -> showMessage(getString(R.string.auth_error_enter_password))
                password != repeatPassword -> showMessage(getString(R.string.auth_error_passwords_do_not_match))
                else -> viewModel.register(email, password, name)
            }
        }

        binding.googleRegisterButton.setOnClickListener {
            showMessage(getString(R.string.auth_google_registration_soon))
        }

        binding.vkRegisterButton.setOnClickListener {
            showMessage(getString(R.string.auth_vk_registration_soon))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.registerSubmitButton.isEnabled = state !is AuthUiState.Loading
                when (state) {
                    is AuthUiState.Authenticated -> {
                        viewModel.resetState()
                        openFragment(RegistrationSuccessFragment())
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
            context = requireContext(),
            authApi = ApiClient.create(AuthApi::class.java),
            sessionStorage = UserSessionStorage(requireContext()),
        )
        return ViewModelProvider(
            this,
            AuthViewModelFactory(requireContext(), repository)
        )[AuthViewModel::class.java]
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
