package com.hse.polochka.feature.shopping.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemShoppingHistoryBinding
import com.hse.polochka.feature.shopping.presentation.model.ShoppingHistoryUi

class ShoppingHistoryAdapter(
    private val items: List<ShoppingHistoryUi>,
    private val onClick: (ShoppingHistoryUi) -> Unit
) : RecyclerView.Adapter<ShoppingHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemShoppingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.dateTextView.text = item.date

        holder.binding.root.setOnClickListener {
            onClick(item)
        }
    }
}