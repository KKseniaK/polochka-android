package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemRecipeTagBinding

class RecipeTagAdapter(
    private val items: List<String>,
    private val selectedItem: String? = null,
    private val onTagClick: ((String) -> Unit)? = null,
) : RecyclerView.Adapter<RecipeTagAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeTagBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRecipeTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = item == selectedItem
        val context = holder.binding.root.context

        holder.binding.tagTextView.text = RecipeTagFormatter.readable(item)
        holder.binding.tagTextView.setBackgroundResource(
            if (isSelected) R.drawable.shape_recipe_tag_selected else R.drawable.shape_recipe_tag
        )
        holder.binding.tagTextView.setTextColor(
            ContextCompat.getColor(
                context,
                if (isSelected) R.color.background_primary else R.color.text_primary,
            )
        )
        holder.binding.tagTextView.setOnClickListener {
            onTagClick?.invoke(item)
        }
    }
}
