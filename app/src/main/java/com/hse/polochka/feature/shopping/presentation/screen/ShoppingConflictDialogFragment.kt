package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hse.polochka.databinding.DialogShoppingMessageBinding

class ShoppingConflictDialogFragment : DialogFragment() {

    var onPrimaryClick: (() -> Unit)? = null
    var onSecondaryClick: (() -> Unit)? = null

    private var _binding: DialogShoppingMessageBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var title: String = ""
    private var message: String = ""
    private var primaryText: String? = null
    private var secondaryText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogShoppingMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.titleTextView.text = title
        binding.messageTextView.text = message
        binding.closeButton.setOnClickListener { dismiss() }
        bindActionButton(binding.primaryButton, primaryText) {
            onPrimaryClick?.invoke()
            dismiss()
        }
        bindActionButton(binding.secondaryButton, secondaryText) {
            onSecondaryClick?.invoke()
            dismiss()
        }
    }

    private fun bindActionButton(button: android.widget.Button, text: String?, onClick: () -> Unit) {
        button.visibility = if (text == null) View.GONE else View.VISIBLE
        button.text = text.orEmpty()
        button.setOnClickListener { onClick() }
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
        fun newInstance(
            title: String,
            message: String,
            primaryText: String? = null,
            secondaryText: String? = null,
        ): ShoppingConflictDialogFragment =
            ShoppingConflictDialogFragment().apply {
                this.title = title
                this.message = message
                this.primaryText = primaryText
                this.secondaryText = secondaryText
            }
    }
}
