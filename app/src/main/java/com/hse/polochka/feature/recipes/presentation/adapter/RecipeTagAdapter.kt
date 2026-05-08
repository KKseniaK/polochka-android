package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemRecipeTagBinding

class RecipeTagAdapter(
    private val items: List<String>
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
        holder.binding.tagTextView.text = items[position]
    }
}
