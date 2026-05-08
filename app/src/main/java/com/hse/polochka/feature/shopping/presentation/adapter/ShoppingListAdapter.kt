package com.hse.polochka.feature.shopping.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemShoppingCheckboxBinding
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class ShoppingListAdapter(
    private val items: MutableList<ShoppingItemUi>,
    private val showDeleteButton: Boolean = true,
    private val isPreview: Boolean = false,
    private val onCheckedChange: (ShoppingItemUi, Boolean) -> Boolean = { _, _ -> true },
    private val onDeleteClick: (ShoppingItemUi) -> Boolean = { _ -> true },
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemShoppingCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.deleteButton.visibility =
            if (showDeleteButton) View.VISIBLE else View.GONE

        holder.binding.checkBox.setOnCheckedChangeListener(null)

        holder.binding.checkBox.text = item.title
        holder.binding.checkBox.isChecked = item.isChecked
        holder.binding.checkBox.isEnabled = true
        holder.binding.checkBox.isClickable = !isPreview
        holder.binding.checkBox.isFocusable = !isPreview

        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener

            val currentItem = items[currentPosition]
            val accepted = onCheckedChange(currentItem, isChecked)
            if (accepted) {
                items[currentPosition] = currentItem.copy(isChecked = isChecked)
            } else {
                notifyItemChanged(currentPosition)
            }
        }

        holder.binding.deleteButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            if (!onDeleteClick(items[currentPosition])) return@setOnClickListener

            items.removeAt(currentPosition)
            notifyItemRemoved(currentPosition)
        }
    }

    fun addItem(title: String) {
        if (title.isBlank()) return

        val newItem = ShoppingItemUi(
            id = (items.maxOfOrNull { it.id } ?: 0) + 1,
            title = title.trim()
        )

        items.add(newItem)
        notifyItemInserted(items.lastIndex)
    }

    fun submitItems(updatedItems: List<ShoppingItemUi>) {
        items.clear()
        items.addAll(updatedItems)
        notifyDataSetChanged()
    }

    fun removeItem(item: ShoppingItemUi) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index == -1) return

        items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun currentItems(): List<ShoppingItemUi> = items.toList()
}
