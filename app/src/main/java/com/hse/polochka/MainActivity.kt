package com.hse.polochka

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.databinding.ActivityMainBinding
import com.hse.polochka.feature.analytics.presentation.screen.AnalyticsFragment
import com.hse.polochka.feature.auth.data.remote.AuthApi
import com.hse.polochka.feature.auth.data.repository.AuthRepositoryImpl
import com.hse.polochka.feature.auth.presentation.screen.WelcomeFragment
import com.hse.polochka.feature.auth.presentation.state.AuthUiState
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModel
import com.hse.polochka.feature.auth.presentation.viewmodel.AuthViewModelFactory
import com.hse.polochka.feature.home.presentation.screen.HomeFragment
import com.hse.polochka.feature.recipes.presentation.screen.RecipesFragment
import com.hse.polochka.feature.shopping.presentation.screen.ShoppingFragment
import com.hse.polochka.feature.storage.presentation.screen.StorageFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = createAuthViewModel()

        if (savedInstanceState == null) {
            openFragment(WelcomeFragment())
            hideBottomMenu()
            restoreSession()
        }

        setupBottomMenu()
    }

    private fun createAuthViewModel(): AuthViewModel {
        val repository = AuthRepositoryImpl(
            context = this,
            authApi = ApiClient.create(AuthApi::class.java),
            sessionStorage = UserSessionStorage(this),
        )
        return ViewModelProvider(this, AuthViewModelFactory(this, repository))[AuthViewModel::class.java]
    }

    private fun restoreSession() {
        lifecycleScope.launch {
            authViewModel.state.collect { state ->
                if (state is AuthUiState.Authenticated) {
                    authViewModel.resetState()
                    openHome()
                }
            }
        }
        authViewModel.restoreSession()
    }

    private fun setupBottomMenu() {
        binding.bottomMenu.homeMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.homeMenuButton)
            openFragment(HomeFragment())
        }

        binding.bottomMenu.storageMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.storageMenuButton)
            openFragment(StorageFragment())
        }

        binding.bottomMenu.shoppingMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.shoppingMenuButton)
            openFragment(ShoppingFragment())
        }

        binding.bottomMenu.recipesMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.recipesMenuButton)
            openFragment(RecipesFragment())
        }

        binding.bottomMenu.profileMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.profileMenuButton)
            openFragment(AnalyticsFragment())
        }
    }

    fun openHome() {
        showBottomMenu()
        selectBottomMenuItem(binding.bottomMenu.homeMenuButton)
        openFragment(HomeFragment())
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun selectBottomMenuItem(selectedView: View) {
        val items = listOf(
            binding.bottomMenu.storageMenuButton,
            binding.bottomMenu.shoppingMenuButton,
            binding.bottomMenu.homeMenuButton,
            binding.bottomMenu.recipesMenuButton,
            binding.bottomMenu.profileMenuButton
        )

        items.forEach { it.isSelected = it == selectedView }
    }

    fun showBottomMenu() {
        binding.bottomMenu.root.visibility = View.VISIBLE
    }

    fun hideBottomMenu() {
        binding.bottomMenu.root.visibility = View.GONE
    }
}
