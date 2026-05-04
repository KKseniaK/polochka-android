package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.databinding.DialogShoppingListBinding
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class ShoppingListDialogFragment : DialogFragment() {

    private var _binding: DialogShoppingListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var adapter: ShoppingListAdapter

    private val items = mutableListOf(
        ShoppingItemUi(1, "Яблоки"),
        ShoppingItemUi(2, "Макароны"),
        ShoppingItemUi(3, "Яйца 10 шт.", true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ShoppingListAdapter(
            items = items,
            showDeleteButton = true
        )

        binding.shoppingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.shoppingRecyclerView.adapter = adapter

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.addButton.setOnClickListener {
            val text = binding.inputEditText.text.toString().trim()

            if (text.isNotEmpty()) {
                adapter.addItem(text)
                binding.inputEditText.text?.clear()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val margin = (24 * resources.displayMetrics.density).toInt()

            val height = if (items.size <= 5) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                (resources.displayMetrics.heightPixels * 0.78).toInt()
            }

            setLayout(
                resources.displayMetrics.widthPixels - margin * 2,
                height
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}