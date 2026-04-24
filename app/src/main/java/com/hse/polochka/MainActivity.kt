package com.hse.polochka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hse.polochka.feature.auth.presentation.screen.WelcomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, WelcomeFragment())
                .commit()
        }
    }
}