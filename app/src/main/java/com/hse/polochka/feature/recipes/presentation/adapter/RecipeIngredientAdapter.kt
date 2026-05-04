package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemRecipeIngredientBinding
import com.hse.polochka.feature.recipes.presentation.model.RecipeIngredientUi

class RecipeIngredientAdapter(
    private val items: List<RecipeIngredientUi>
) : RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>() {

    private var portionCount: Int = 1

    inner class ViewHolder(val binding: ItemRecipeIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context

        val color = ContextCompat.getColor(
            context,
            if (item.isAvailable) R.color.button_primary else R.color.storage_last_day
        )

        holder.binding.statusImageView.setImageResource(
            if (item.isAvailable) R.drawable.ic_check else R.drawable.ic_close
        )

        holder.binding.statusImageView.setColorFilter(color)

        holder.binding.nameTextView.text = item.name
        holder.binding.nameTextView.setTextColor(color)

        holder.binding.amountTextView.text = formatAmount(item.amountForOnePortion, portionCount)
        holder.binding.amountTextView.setTextColor(color)
    }

    fun updatePortionCount(newCount: Int) {
        portionCount = newCount
        notifyDataSetChanged()
    }

    private fun formatAmount(baseAmount: String, portionCount: Int): String {
        return if (portionCount == 1) {
            baseAmount
        } else {
            "$baseAmount × $portionCount"
        }
    }
}