package com.hse.polochka.feature.storage.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemStorageProductBinding
import com.hse.polochka.feature.storage.presentation.model.ProductStorageStatus
import com.hse.polochka.feature.storage.presentation.model.StorageProductUi

class StorageAdapter(
    private var items: List<StorageProductUi>,
    private val selectedIds: MutableSet<Int>,
    private val onSelectionChanged: (Set<Int>) -> Unit,
    private val onProductClick: (StorageProductUi) -> Unit,
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
        holder.binding.selectCheckBox.setOnCheckedChangeListener(null)
        holder.binding.selectCheckBox.isChecked = item.id in selectedIds
        holder.binding.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIds.add(item.id)
            } else {
                selectedIds.remove(item.id)
            }
            onSelectionChanged(selectedIds)
        }

        val color = when (item.status) {
            ProductStorageStatus.EXPIRED -> R.color.storage_expired
            ProductStorageStatus.LAST_DAY -> R.color.storage_last_day
            ProductStorageStatus.MIDDLE -> R.color.storage_middle
            ProductStorageStatus.FRESH -> R.color.storage_fresh
            ProductStorageStatus.LONG_LIFE -> R.color.storage_fresh
        }

        holder.binding.cardContainer.setCardBackgroundColor(ContextCompat.getColor(ctx, color))
        holder.binding.cardContainer.setOnClickListener {
            onProductClick(item)
        }

        bindTags(holder, item.tags)
        bindLifeIndicator(holder, item)
    }

    fun submitItems(updatedItems: List<StorageProductUi>) {
        items = updatedItems
        notifyDataSetChanged()
    }

    private fun bindTags(holder: ViewHolder, tags: List<String>) {
        holder.binding.tagsContainer.removeAllViews()
        tags.forEach { tag ->
            val tagView = TextView(holder.binding.root.context).apply {
                text = tag
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                setBackgroundResource(R.drawable.shape_storage_tag)
                setPadding(14, 2, 14, 2)
            }
            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                marginEnd = 8
            }
            holder.binding.tagsContainer.addView(tagView, params)
        }
    }

    private fun bindLifeIndicator(holder: ViewHolder, item: StorageProductUi) {
        val binding = holder.binding
        when (item.status) {
            ProductStorageStatus.EXPIRED -> {
                binding.lifeIndicatorContainer.visibility = View.GONE
                binding.longLifeTextView.visibility = View.GONE
                binding.expiredTextView.visibility = View.VISIBLE
            }
            ProductStorageStatus.LONG_LIFE -> {
                binding.lifeIndicatorContainer.visibility = View.GONE
                binding.expiredTextView.visibility = View.GONE
                binding.longLifeTextView.visibility = View.VISIBLE
            }
            else -> {
                binding.expiredTextView.visibility = View.GONE
                binding.longLifeTextView.visibility = View.GONE
                binding.lifeIndicatorContainer.visibility = View.VISIBLE
                binding.lifeIndicatorContainer.post {
                    val fillHeight = (binding.lifeIndicatorContainer.height * item.lifeProgress)
                        .toInt()
                        .coerceAtLeast(6)
                    binding.lifeIndicatorFill.layoutParams =
                        binding.lifeIndicatorFill.layoutParams.apply {
                            height = fillHeight
                        }
                }
            }
        }
    }
}
