package com.hse.polochka.feature.onboarding.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemPreferenceChipBinding
import com.hse.polochka.feature.onboarding.presentation.model.PreferenceChipUi

class PreferenceChipAdapter(
    private val items: MutableList<PreferenceChipUi>
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

    fun getSelectedIds(): List<Int> {
        return items.filter { it.isSelected }.map { it.id }
    }

    inner class ViewHolder(
        private val binding: ItemPreferenceChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PreferenceChipUi) {

            binding.chipTextView.setText(item.titleResId)

            // состояние выбран/не выбран
            binding.chipTextView.isSelected = item.isSelected

            // цвет текста
            val context = binding.root.context
            val textColor = if (item.isSelected) {
                ContextCompat.getColor(context, R.color.background_primary)
            } else {
                ContextCompat.getColor(context, R.color.text_primary)
            }
            binding.chipTextView.setTextColor(textColor)

            binding.chipTextView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                val current = items[position]
                items[position] = current.copy(isSelected = !current.isSelected)

                notifyItemChanged(position)
            }
        }
    }
}