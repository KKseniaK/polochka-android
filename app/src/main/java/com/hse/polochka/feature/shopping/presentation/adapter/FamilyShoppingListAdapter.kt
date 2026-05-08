package com.hse.polochka.feature.shopping.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.databinding.ItemFamilyShoppingListBinding
import com.hse.polochka.feature.shopping.presentation.model.FamilyShoppingListUi

class FamilyShoppingListAdapter(
    private val items: List<FamilyShoppingListUi>,
    private val onClick: (FamilyShoppingListUi) -> Unit
) : RecyclerView.Adapter<FamilyShoppingListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFamilyShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFamilyShoppingListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.nameTextView.text = item.ownerName

        val previewItems = item.items.take(3)
        val checkBoxes = listOf(
            holder.binding.firstItemCheckBox,
            holder.binding.secondItemCheckBox,
            holder.binding.thirdItemCheckBox
        )

        checkBoxes.forEachIndexed { index, checkBox ->
            val previewItem = previewItems.getOrNull(index)

            if (previewItem == null) {
                checkBox.visibility = View.GONE
                checkBox.text = ""
                checkBox.isChecked = false
            } else {
                checkBox.visibility = View.VISIBLE
                checkBox.text = previewItem.title
                checkBox.isChecked = previewItem.isChecked
            }
            checkBox.isClickable = false
            checkBox.isFocusable = false
            checkBox.isEnabled = true
        }

        holder.binding.root.setOnClickListener {
            onClick(item)
        }
    }
}
