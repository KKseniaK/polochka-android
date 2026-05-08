package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.R
import com.hse.polochka.databinding.DialogShoppingHistoryBinding
import com.hse.polochka.feature.shopping.presentation.adapter.HistoryProductAdapter
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi

class ShoppingHistoryDialogFragment : DialogFragment() {

    private var _binding: DialogShoppingHistoryBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var date: String = ""
    private val items = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogShoppingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.titleTextView.text = getString(R.string.shopping_history_receipt_title, date)
        binding.historyItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyItemsRecyclerView.adapter = HistoryProductAdapter(items)
        binding.closeButton.setOnClickListener { dismiss() }
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
        fun newInstance(history: ShoppingHistoryUi): ShoppingHistoryDialogFragment =
            ShoppingHistoryDialogFragment().apply {
                date = history.date
                items.addAll(history.items)
            }
    }
}
