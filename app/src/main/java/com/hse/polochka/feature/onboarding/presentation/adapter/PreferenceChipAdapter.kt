package com.hse.polochka.feature.onboarding.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemPreferenceChipBinding
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi

class PreferenceChipAdapter(
    private val items: MutableList<PreferenceChipUi>,
    private val onSelectionChanged: (List<String>) -> Unit = {},
) : RecyclerView.Adapter<PreferenceChipAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPreferenceChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedIds(): List<String> {
        return items.filter { it.isSelected }.map { it.id }
    }

    inner class ViewHolder(
        private val binding: ItemPreferenceChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PreferenceChipUi) {

            binding.chipTextView.setText(item.titleResId)

            if (item.iconResId != null) {
                binding.chipIconImageView.setImageResource(item.iconResId)
                binding.chipIconImageView.visibility = View.VISIBLE
            } else {
                binding.chipIconImageView.visibility = View.GONE
            }

            // состояние
            binding.root.isSelected = item.isSelected

            // клик
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                val current = items[position]
                items[position] = current.copy(isSelected = !current.isSelected)

                notifyItemChanged(position)
                onSelectionChanged(getSelectedIds())
            }
        }
    }
}
