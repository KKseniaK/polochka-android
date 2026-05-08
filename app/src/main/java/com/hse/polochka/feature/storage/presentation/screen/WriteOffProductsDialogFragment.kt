package com.hse.polochka.feature.storage.presentation.screen

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.hse.polochka.R
import com.hse.polochka.databinding.DialogWriteOffProductsBinding
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi
import com.hse.polochka.feature.storage.presentation.model.WriteOffResult

class WriteOffProductsDialogFragment : DialogFragment() {

    var products: List<StorageProductUi> = emptyList()
    var onCompleted: ((List<WriteOffResult>) -> Unit)? = null

    private var _binding: DialogWriteOffProductsBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val results = mutableListOf<WriteOffResult>()
    private var currentIndex = 0
    private var isAnimating = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = DialogWriteOffProductsBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9f).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }
        setupClickListeners()
        renderCurrentProduct()
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener { dismiss() }
        binding.spoiledButton.setOnClickListener { applyReason(REASON_SPOILED) }
        binding.eatenButton.setOnClickListener { applyReason(REASON_EATEN) }
        binding.customReasonButton.setOnClickListener { showCustomReasonDialog() }
        binding.allSpoiledButton.setOnClickListener { applyReasonToRemaining(REASON_SPOILED) }
        binding.allEatenButton.setOnClickListener { applyReasonToRemaining(REASON_EATEN) }
    }

    private fun renderCurrentProduct() {
        val product = products.getOrNull(currentIndex) ?: run {
            completeFlow()
            return
        }

        binding.progressTextView.text = getString(
            R.string.storage_write_off_progress,
            currentIndex + 1,
            products.size,
        )
        binding.productImageView.setImageResource(product.imageResId)
        binding.productNameTextView.text = product.name
        binding.productAmountTextView.text = product.amount
        binding.productTagsTextView.text = product.tags.joinToString(separator = " / ")
    }

    private fun applyReason(reason: String) {
        if (isAnimating) return

        val product = products.getOrNull(currentIndex) ?: return
        results += WriteOffResult(productId = product.id, reason = reason)
        animateCurrentCardAway {
            currentIndex += 1
            if (currentIndex >= products.size) {
                completeFlow()
            } else {
                renderCurrentProduct()
                animateCurrentCardIn()
            }
        }
    }

    private fun applyReasonToRemaining(reason: String) {
        if (isAnimating) return

        products.drop(currentIndex).forEach { product ->
            results += WriteOffResult(productId = product.id, reason = reason)
        }
        animateCurrentCardAway {
            completeFlow()
        }
    }

    private fun showCustomReasonDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.storage_custom_reason_hint)
            setSingleLine(false)
            minLines = 2
            setPadding(32, 16, 32, 16)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.storage_custom_reason_title)
            .setView(input)
            .setPositiveButton(R.string.storage_custom_reason_save) { _, _ ->
                val reasonText = input.text.toString().trim()
                if (reasonText.isNotEmpty()) {
                    applyReason("$REASON_CUSTOM_PREFIX$reasonText")
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun animateCurrentCardAway(onEnd: () -> Unit) {
        isAnimating = true
        setActionsEnabled(false)
        binding.productCardContainer.animate()
            .translationX(binding.root.width.toFloat())
            .alpha(0f)
            .setDuration(180L)
            .withEndAction(onEnd)
            .start()
    }

    private fun animateCurrentCardIn() {
        binding.productCardContainer.translationX = -binding.root.width * 0.2f
        binding.productCardContainer.alpha = 0f
        binding.productCardContainer.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(160L)
            .withEndAction {
                isAnimating = false
                setActionsEnabled(true)
            }
            .start()
    }

    private fun setActionsEnabled(isEnabled: Boolean) {
        binding.spoiledButton.isEnabled = isEnabled
        binding.eatenButton.isEnabled = isEnabled
        binding.customReasonButton.isEnabled = isEnabled
        binding.allSpoiledButton.isEnabled = isEnabled
        binding.allEatenButton.isEnabled = isEnabled
    }

    private fun completeFlow() {
        onCompleted?.invoke(results.toList())
        dismiss()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val REASON_SPOILED = "spoiled"
        private const val REASON_EATEN = "eaten"
        private const val REASON_CUSTOM_PREFIX = "custom:"
    }
}
