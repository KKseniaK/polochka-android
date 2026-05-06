package com.hse.polochka.feature.home.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemHomeExpiringProductBinding
import com.hse.polochka.feature.home.presentation.model.HomeExpiringProductUi

class HomeExpiringProductsAdapter(
    private val items: List<HomeExpiringProductUi>
) : RecyclerView.Adapter<HomeExpiringProductsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHomeExpiringProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeExpiringProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.iconImageView.setImageResource(item.iconResId)
        holder.binding.nameTextView.text = item.name
        holder.binding.dateTextView.text = item.daysText
    }
}