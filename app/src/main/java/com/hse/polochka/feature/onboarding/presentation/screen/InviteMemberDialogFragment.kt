package com.hse.polochka.feature.onboarding.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R

class InviteMemberDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_invite_member, container, false)

        val close = view.findViewById<ImageView>(R.id.closeButton)
        val confirm = view.findViewById<View>(R.id.confirmButton)
        val email = view.findViewById<EditText>(R.id.emailEditText)

        close.setOnClickListener { dismiss() }

        confirm.setOnClickListener {
            val emailText = email.text.toString().trim()

            if (emailText.isBlank()) {
                email.error = "Введите email"
                return@setOnClickListener
            }

            // TODO: отправка приглашения
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val margin = (20 * resources.displayMetrics.density).toInt()

            setLayout(
                resources.displayMetrics.widthPixels - margin * 2,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}