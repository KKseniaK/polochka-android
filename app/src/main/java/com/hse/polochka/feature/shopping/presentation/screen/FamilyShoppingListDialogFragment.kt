package com.hse.polochka.feature.shopping.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hse.polochka.databinding.DialogFamilyShoppingListBinding
import com.hse.polochka.feature.shopping.presentation.adapter.ShoppingListAdapter
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingListUi
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class FamilyShoppingListDialogFragment : DialogFragment() {

    var onBuyItem: ((ShoppingItemUi, Boolean) -> Boolean)? = null

    private var _binding: DialogFamilyShoppingListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var ownerName: String = ""
    private val items = mutableListOf<ShoppingItemUi>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFamilyShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nameTextView.text = ownerName

        binding.shoppingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.shoppingRecyclerView.adapter = ShoppingListAdapter(
            items = items,
            showDeleteButton = false,
            onCheckedChange = { item, isChecked ->
                onBuyItem?.invoke(item, isChecked) ?: true
            },
        )

        binding.closeButton.setOnClickListener {
            dismiss()
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
                (resources.displayMetrics.heightPixels * 0.72).toInt()
            }

            setLayout(resources.displayMetrics.widthPixels - margin * 2, height)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(list: FamilyShoppingListUi): FamilyShoppingListDialogFragment =
            FamilyShoppingListDialogFragment().apply {
                ownerName = list.ownerName
                items.addAll(list.items)
            }
    }
}
