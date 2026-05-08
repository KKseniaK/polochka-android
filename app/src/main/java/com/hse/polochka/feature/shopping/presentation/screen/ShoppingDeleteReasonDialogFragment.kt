package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R
import com.hse.polochka.databinding.DialogShoppingDeleteReasonBinding

class ShoppingDeleteReasonDialogFragment : DialogFragment() {

    var onBoughtClick: ((Boolean) -> Unit)? = null
    var onNotNeededClick: ((Boolean) -> Unit)? = null

    private var _binding: DialogShoppingDeleteReasonBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var itemTitle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogShoppingDeleteReasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.messageTextView.text = getString(R.string.shopping_delete_reason_message, itemTitle)
        binding.closeButton.setOnClickListener { dismiss() }
        binding.boughtButton.setOnClickListener {
            onBoughtClick?.invoke(binding.rememberCheckBox.isChecked)
            dismiss()
        }
        binding.notNeededButton.setOnClickListener {
            onNotNeededClick?.invoke(binding.rememberCheckBox.isChecked)
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
        fun newInstance(itemTitle: String): ShoppingDeleteReasonDialogFragment =
            ShoppingDeleteReasonDialogFragment().apply {
                this.itemTitle = itemTitle
            }
    }
}
