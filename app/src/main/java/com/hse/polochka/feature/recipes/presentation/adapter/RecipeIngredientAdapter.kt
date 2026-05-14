package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemRecipeIngredientBinding
import com.hse.polochka.feature.recipes.presentation.model.RecipeIngredientUi

class RecipeIngredientAdapter(
    private val items: List<RecipeIngredientUi>,
) : RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>() {

    private var portionCount: Int = 1

    inner class ViewHolder(val binding: ItemRecipeIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
        val isNeutral = item.kind != "product" || !item.isRequiredForAvailability

        val color = ContextCompat.getColor(
            context,
            when {
                isNeutral -> R.color.text_primary
                item.isAvailable -> R.color.button_primary
                else -> R.color.storage_last_day
            },
        )

        if (isNeutral) {
            holder.binding.statusImageView.setImageResource(R.drawable.ic_recipe_neutral_dot)
            holder.binding.statusImageView.clearColorFilter()
        } else {
            holder.binding.statusImageView.setImageResource(
                if (item.isAvailable) R.drawable.ic_check else R.drawable.ic_close
            )
            holder.binding.statusImageView.setColorFilter(color)
        }

        holder.binding.nameTextView.text = item.name.capitalizedIngredientName()
        holder.binding.nameTextView.setTextColor(color)

        holder.binding.amountTextView.text = formatAmount(item.amountForOnePortion, portionCount)
        holder.binding.amountTextView.setTextColor(color)
    }

    fun updatePortionCount(newCount: Int) {
        portionCount = newCount
        notifyDataSetChanged()
    }

    private fun formatAmount(baseAmount: String, portionCount: Int): String {
        if (portionCount == 1 || baseAmount.isTasteAmount()) return baseAmount

        var hasNumber = false
        val scaled = NUMBER_PATTERN.replace(baseAmount) { match ->
            hasNumber = true
            val rawNumber = match.value
            val amount = rawNumber.replace(',', '.').toDoubleOrNull() ?: return@replace rawNumber
            formatNumber(amount * portionCount)
        }
        return if (hasNumber) scaled else "$baseAmount x $portionCount"
    }

    private fun String.isTasteAmount(): Boolean {
        val normalized = lowercase()
        return normalized.contains("по вкусу") || normalized.contains("щепот")
    }

    private fun formatNumber(value: Double): String =
        if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            String.format(java.util.Locale.US, "%.1f", value).replace('.', ',')
        }

    private fun String.capitalizedIngredientName(): String {
        val trimmed = trim()
        if (trimmed.isEmpty()) return trimmed
        return trimmed.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }

    private companion object {
        val NUMBER_PATTERN = Regex("""\d+(?:[.,]\d+)?""")
    }
}
