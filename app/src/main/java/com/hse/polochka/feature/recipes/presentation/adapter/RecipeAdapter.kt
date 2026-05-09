package com.hse.polochka.feature.recipes.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.feature.recipes.presentation.model.RecipeUi

class RecipeAdapter(
    private val items: List<RecipeUi>,
    private val displayMode: DisplayMode = DisplayMode.Compact,
    private val onRecipeClick: (RecipeUi) -> Unit,
) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    enum class DisplayMode {
        Compact,
        Wide,
    }

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val recipeImageView: ImageView = root.findViewById(R.id.recipeImageView)
        val placeholderIconView: ImageView = root.findViewById(R.id.placeholderIconView)
        val favoriteButton: ImageView = root.findViewById(R.id.favoriteButton)
        val titleTextView: TextView = root.findViewById(R.id.titleTextView)
        val statusTextView: TextView = root.findViewById(R.id.statusTextView)
        val personalizationTextView: TextView = root.findViewById(R.id.personalizationTextView)
        val timeTextView: TextView = root.findViewById(R.id.timeTextView)
        val categoryTextView: TextView = root.findViewById(R.id.categoryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (displayMode) {
            DisplayMode.Compact -> R.layout.item_recipe_card
            DisplayMode.Wide -> R.layout.item_recipe_card_wide
        }
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.itemView.setOnClickListener {
            onRecipeClick(item)
        }

        val imageResId = item.imageResId
        holder.recipeImageView.isVisible = imageResId != null
        holder.placeholderIconView.isVisible = imageResId == null
        if (imageResId == null) {
            holder.placeholderIconView.setImageResource(item.placeholderIconResId)
            holder.placeholderIconView.setBackgroundColor(
                ContextCompat.getColor(context, item.placeholderColorResId)
            )
        } else {
            holder.recipeImageView.setImageResource(imageResId)
            holder.placeholderIconView.setBackgroundColor(
                ContextCompat.getColor(context, item.placeholderColorResId)
            )
        }

        holder.titleTextView.text = item.title
        holder.statusTextView.text = item.status
        holder.personalizationTextView.text = item.personalizedStatus
        holder.personalizationTextView.isVisible = item.personalizedStatus != null
        holder.personalizationTextView.setTextColor(
            ContextCompat.getColor(
                context,
                if (item.hasPreferenceConflict) R.color.storage_last_day else R.color.accent_primary,
            )
        )
        holder.timeTextView.text = item.time
        holder.categoryTextView.text = item.category

        holder.favoriteButton.setImageResource(
            if (item.isFavorite) R.drawable.ic_heart_filled
            else R.drawable.ic_heart
        )
    }
}
