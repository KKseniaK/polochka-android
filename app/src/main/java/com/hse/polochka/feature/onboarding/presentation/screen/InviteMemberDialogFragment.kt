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
import androidx.lifecycle.lifecycleScope
import com.hse.polochka.R
import com.hse.polochka.core.family.FamilyStorage
import com.hse.polochka.core.network.ApiClient
import com.hse.polochka.core.network.AuthHeaderProvider
import com.hse.polochka.core.storage.UserSessionStorage
import com.hse.polochka.feature.family.data.remote.FamilyApi
import com.hse.polochka.feature.family.data.repository.FamilyRepositoryImpl
import kotlinx.coroutines.launch

class InviteMemberDialogFragment : DialogFragment() {

    var onInvitationCreated: (() -> Unit)? = null

    private lateinit var familyStorage: FamilyStorage
    private lateinit var familyRepository: FamilyRepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_invite_member, container, false)
        familyStorage = FamilyStorage(requireContext())
        familyRepository = FamilyRepositoryImpl(
            familyApi = ApiClient.create(FamilyApi::class.java),
            familyStorage = familyStorage,
            authHeaderProvider = AuthHeaderProvider(UserSessionStorage(requireContext())),
        )

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

            confirm.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                val inviteLink = runCatching {
                    familyRepository.inviteMember(emailText)
                }.getOrElse {
                    familyStorage.addInvitedMember(emailText)
                    familyStorage.createInviteLink(emailText)
                }

                openEmailInvite(emailText, inviteLink)
                onInvitationCreated?.invoke()
                Toast.makeText(requireContext(), getString(R.string.invite_created), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        return view
    }

    private fun openEmailInvite(email: String, inviteLink: String) {
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
