package com.hse.polochka.feature.shopping.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemShoppingCheckboxBinding
import com.hse.polochka.feature.shopping.presentation.model.ShoppingItemUi

class ShoppingListAdapter(
    private val items: MutableList<ShoppingItemUi>,
    private val showDeleteButton: Boolean = true
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

        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener

            items[currentPosition] = items[currentPosition].copy(isChecked = isChecked)
        }

        holder.binding.deleteButton.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

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
}