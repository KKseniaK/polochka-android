package com.hse.polochka.feature.recipes.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemRecipeStepBinding
import com.hse.polochka.feature.recipes.presentation.model.RecipeStepUi

class RecipeStepAdapter(
    private val items: MutableList<RecipeStepUi>,
) : RecyclerView.Adapter<RecipeStepAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeStepBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeStepBinding.inflate(
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

        holder.binding.stepTitleTextView.text = "ШАГ ${item.stepNumber}"
        holder.binding.stepTextTextView.text = item.text
        holder.binding.stepTextTextView.visibility =
            if (item.isExpanded) View.VISIBLE else View.GONE
        holder.binding.expandButton.rotation = if (item.isExpanded) 180f else 0f
        holder.binding.soundButton.visibility = View.GONE

        val colorRes = if (item.isExpanded) {
            R.color.recipe_step_active
        } else {
            R.color.recipe_step_inactive
        }
        holder.binding.stepHeader.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))

        holder.binding.stepHeader.setOnClickListener {
            toggleStep(holder.bindingAdapterPosition)
        }
        holder.binding.expandButton.setOnClickListener {
            toggleStep(holder.bindingAdapterPosition)
        }
    }

    private fun toggleStep(position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        items.forEachIndexed { index, item ->
            items[index] = item.copy(isExpanded = index == position && !item.isExpanded)
        }
        notifyDataSetChanged()
    }
}
