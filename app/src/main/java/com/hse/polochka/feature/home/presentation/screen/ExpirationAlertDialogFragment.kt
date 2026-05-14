package com.hse.polochka.feature.home.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R
import com.hse.polochka.databinding.DialogExpirationAlertBinding

class ExpirationAlertDialogFragment : DialogFragment() {

    var onOpenStorageClick: (() -> Unit)? = null

    private var _binding: DialogExpirationAlertBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogExpirationAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.titleTextView.text = getString(R.string.expiration_alert_title)
        binding.messageTextView.text = requireArguments().getString(ARG_MESSAGE).orEmpty()
        binding.closeButton.setOnClickListener { dismiss() }
        binding.secondaryButton.setOnClickListener { dismiss() }
        binding.primaryButton.setOnClickListener {
            onOpenStorageClick?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val margin = (24 * resources.displayMetrics.density).toInt()
            setLayout(resources.displayMetrics.widthPixels - margin * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_MESSAGE = "message"

        fun newInstance(message: String): ExpirationAlertDialogFragment =
            ExpirationAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                }
            }
    }
}
