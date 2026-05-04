package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemRecipeCardBinding
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class RecipeAdapter(
    private val items: List<RecipeUi>,
    private val onRecipeClick: (RecipeUi) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.root.setOnClickListener {
            onRecipeClick(item)
        }

        holder.binding.recipeImageView.setImageResource(item.imageResId)
        holder.binding.titleTextView.text = item.title
        holder.binding.statusTextView.text = item.status
        holder.binding.timeTextView.text = item.time
        holder.binding.categoryTextView.text = item.category

        holder.binding.favoriteButton.setImageResource(
            if (item.isFavorite) R.drawable.ic_heart_filled
            else R.drawable.ic_heart
        )
    }
}