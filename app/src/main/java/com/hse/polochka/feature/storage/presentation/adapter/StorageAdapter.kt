package com.hse.polochka.feature.storage.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemStorageProductBinding
import com.hse.polochka.feature.storage.presentation.model.ProductStorageStatus
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi

class StorageAdapter(
    private val items: List<StorageProductUi>,
    private val onProductClick: (StorageProductUi) -> Unit
) : RecyclerView.Adapter<StorageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStorageProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStorageProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.binding.root.context

        holder.binding.nameTextView.text = item.name
        holder.binding.amountTextView.text = item.amount
        holder.binding.daysTextView.text = item.daysLeftText
        holder.binding.productImageView.setImageResource(item.imageResId)

        val color = when (item.status) {
            ProductStorageStatus.EXPIRED -> R.color.storage_expired
            ProductStorageStatus.LAST_DAY -> R.color.storage_last_day
            ProductStorageStatus.MIDDLE -> R.color.storage_middle
            ProductStorageStatus.FRESH -> R.color.storage_fresh
        }

        holder.binding.root.setOnClickListener {
            onProductClick(item)
        }

        holder.binding.cardContainer.setCardBackgroundColor(
            ContextCompat.getColor(ctx, color)
        )
    }
}