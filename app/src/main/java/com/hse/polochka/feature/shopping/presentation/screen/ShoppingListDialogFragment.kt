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

    enum class Mode {
        OWN,
        FAMILY,
    }

    var onItemsChanged: ((List<ShoppingItemUi>) -> Unit)? = null
    var onAddItem: ((String) -> Unit)? = null
    var onCheckChanged: ((ShoppingItemUi, Boolean) -> Boolean)? = null
    var onDeleteItem: ((ShoppingItemUi) -> Boolean)? = null
    var onDeleteBoughtToStorage: ((ShoppingItemUi) -> Unit)? = null
    var onMoveBoughtToStorage: ((List<ShoppingItemUi>) -> Unit)? = null
    var askBeforeDelete: Boolean = true
    var onRememberDeleteChoiceChanged: ((Boolean) -> Unit)? = null

    private var _binding: DialogShoppingListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private lateinit var adapter: ShoppingListAdapter
    private var title: String = ""
    private var mode: Mode = Mode.OWN
    private val items = mutableListOf<ShoppingItemUi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.titleTextView.text = title
        binding.addContainer.visibility = if (mode == Mode.OWN) View.VISIBLE else View.GONE
        binding.moveBoughtToStorageButton.visibility = if (mode == Mode.OWN) View.VISIBLE else View.GONE
        binding.storageCareTextView.visibility = if (mode == Mode.OWN) View.VISIBLE else View.GONE

        adapter = ShoppingListAdapter(
            items = items,
            showDeleteButton = mode == Mode.OWN,
            onCheckedChange = { item, isChecked ->
                val accepted = onCheckChanged?.invoke(item, isChecked) ?: true
                if (accepted) {
                    binding.root.post {
                        notifyItemsChanged()
                    }
                }
                accepted
            },
            onDeleteClick = { item ->
                handleDeleteClick(item)
            },
        )

        binding.shoppingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.shoppingRecyclerView.adapter = adapter
        limitListHeightIfNeeded()

        binding.closeButton.setOnClickListener {
            notifyItemsChanged()
            dismiss()
        }

        binding.addButton.setOnClickListener {
            val text = binding.inputEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                adapter.addItem(text)
                onAddItem?.invoke(text)
                binding.inputEditText.text?.clear()
                notifyItemsChanged()
                updateMoveToStorageButton()
            }
        }

        binding.moveBoughtToStorageButton.setOnClickListener {
            val boughtItems = adapter.currentItems().filter { it.isChecked }
            if (boughtItems.isEmpty()) return@setOnClickListener

            onMoveBoughtToStorage?.invoke(boughtItems)
            dismiss()
        }

        updateMoveToStorageButton()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val margin = (24 * resources.displayMetrics.density).toInt()
            setLayout(resources.displayMetrics.widthPixels - margin * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun limitListHeightIfNeeded() {
        if (items.size <= MAX_VISIBLE_ITEMS_WITHOUT_SCROLL) return

        binding.shoppingRecyclerView.layoutParams = binding.shoppingRecyclerView.layoutParams.apply {
            height = (resources.displayMetrics.heightPixels * 0.42).toInt()
        }
    }

    private fun notifyItemsChanged() {
        onItemsChanged?.invoke(items.toList())
        updateMoveToStorageButton()
    }

    private fun handleDeleteClick(item: ShoppingItemUi): Boolean {
        if (mode != Mode.OWN || !askBeforeDelete) {
            return onDeleteItem?.invoke(item) ?: true
        }

        showDeleteReasonDialog(item)
        return false
    }

    private fun showDeleteReasonDialog(item: ShoppingItemUi) {
        ShoppingDeleteReasonDialogFragment
            .newInstance(item.title)
            .apply {
                onBoughtClick = { shouldRemember ->
                    if (shouldRemember) {
                        onRememberDeleteChoiceChanged?.invoke(false)
                    }
                    if (onDeleteBoughtToStorage == null) {
                        onMoveBoughtToStorage?.invoke(listOf(item))
                    } else {
                        onDeleteBoughtToStorage?.invoke(item)
                    }
                    removeItemFromDialog(item)
                }
                onNotNeededClick = { shouldRemember ->
                    if (shouldRemember) {
                        onRememberDeleteChoiceChanged?.invoke(false)
                    }
                    onDeleteItem?.invoke(item)
                    removeItemFromDialog(item)
                }
            }
            .show(parentFragmentManager, "shopping_delete_reason")
    }

    private fun removeItemFromDialog(item: ShoppingItemUi) {
        adapter.removeItem(item)
        items.removeAll { it.id == item.id }
        notifyItemsChanged()
    }

    private fun updateMoveToStorageButton() {
        if (!::adapter.isInitialized || _binding == null) return

        val hasBoughtItems = adapter.currentItems().any { it.isChecked }
        binding.moveBoughtToStorageButton.isEnabled = hasBoughtItems
        binding.moveBoughtToStorageButton.alpha = if (hasBoughtItems) 1f else 0.45f
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val MAX_VISIBLE_ITEMS_WITHOUT_SCROLL = 6

        fun newInstance(
            title: String,
            items: List<ShoppingItemUi>,
            mode: Mode,
        ): ShoppingListDialogFragment =
            ShoppingListDialogFragment().apply {
                this.title = title
                this.mode = mode
                this.items.addAll(items)
            }
    }
}
