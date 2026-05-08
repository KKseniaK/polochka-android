package com.hse.polochka.feature.auth.presentation.screen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hse.polochka.R

class ForgotPasswordFragment : Fragment(R.layout.activity_auth_forgot_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sendButton = view.findViewById<View>(R.id.sendButton)
        val backTextView = view.findViewById<TextView>(R.id.backTextView)

        sendButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.auth_password_recovery_soon),
                Toast.LENGTH_SHORT
            ).show()
        }

        backTextView.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
