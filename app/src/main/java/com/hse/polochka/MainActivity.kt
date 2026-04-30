package com.hse.polochka

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hse.polochka.databinding.ActivityMainBinding
import com.hse.polochka.feature.auth.presentation.screen.WelcomeFragment
import com.hse.polochka.feature.home.presentation.screen.HomeFragment

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

    // -------------------------------
    // НАСТРОЙКА МЕНЮ
    // -------------------------------
    private fun setupBottomMenu() {

        binding.bottomMenu.homeMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.homeMenuButton)
            openFragment(HomeFragment())
        }

        binding.bottomMenu.storageMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.storageMenuButton)
            // TODO: StorageFragment()
        }

        binding.bottomMenu.shoppingMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.shoppingMenuButton)
            // TODO: ShoppingFragment()
        }

        binding.bottomMenu.recipesMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.recipesMenuButton)
            // TODO: RecipesFragment()
        }

        binding.bottomMenu.profileMenuButton.setOnClickListener {
            selectBottomMenuItem(binding.bottomMenu.profileMenuButton)
            // TODO: ProfileFragment()
        }
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