package com.hse.polochka

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hse.polochka.databinding.ActivityMainBinding
import com.hse.polochka.feature.analytics.presentation.screen.AnalyticsFragment
import com.hse.polochka.feature.auth.presentation.screen.WelcomeFragment
import com.hse.polochka.feature.home.presentation.screen.HomeFragment
import com.hse.polochka.feature.shopping.presentation.screen.ShoppingFragment
import com.hse.polochka.feature.storage.presentation.screen.StorageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // стартовый экран
        if (savedInstanceState == null) {
            openFragment(WelcomeFragment())
            hideBottomMenu()
        }

        setupBottomMenu()
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

    // -------------------------------
    // ПЕРЕКЛЮЧЕНИЕ ЭКРАНОВ
    // -------------------------------
    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // -------------------------------
    // ВЫДЕЛЕНИЕ АКТИВНОЙ КНОПКИ
    // -------------------------------
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

    // -------------------------------
    // ПОКАЗ / СКРЫТИЕ МЕНЮ
    // -------------------------------
    fun showBottomMenu() {
        binding.bottomMenu.root.visibility = View.VISIBLE
    }

    fun hideBottomMenu() {
        binding.bottomMenu.root.visibility = View.GONE
    }
}