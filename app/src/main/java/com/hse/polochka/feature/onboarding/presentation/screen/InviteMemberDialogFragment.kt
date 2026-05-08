package com.hse.polochka.feature.onboarding.presentation.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R
import com.hse.polochka.core.family.FamilyStorage

class InviteMemberDialogFragment : DialogFragment() {

    var onInvitationCreated: (() -> Unit)? = null

    private lateinit var familyStorage: FamilyStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_invite_member, container, false)
        familyStorage = FamilyStorage(requireContext())

        val close = view.findViewById<ImageView>(R.id.closeButton)
        val confirm = view.findViewById<View>(R.id.confirmButton)
        val email = view.findViewById<EditText>(R.id.emailEditText)

        close.setOnClickListener { dismiss() }

        confirm.setOnClickListener {
            val emailText = email.text.toString().trim()

            if (emailText.isBlank()) {
                email.error = getString(R.string.invite_error_empty_email)
                return@setOnClickListener
            }

            familyStorage.addInvitedMember(emailText)
            openEmailInvite(emailText)
            onInvitationCreated?.invoke()
            Toast.makeText(requireContext(), getString(R.string.invite_created), Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }

    private fun openEmailInvite(email: String) {
        val inviteLink = familyStorage.createInviteLink(email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_email_body, inviteLink))
        }

        runCatching {
            startActivity(intent)
        }
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
