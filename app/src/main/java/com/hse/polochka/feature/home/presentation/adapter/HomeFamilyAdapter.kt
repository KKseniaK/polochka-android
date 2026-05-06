package com.hse.polochka.feature.home.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hse.polochka.R
import com.hse.polochka.databinding.ItemHomeFamilyMemberBinding
import com.hse.polochka.feature.home.presentation.model.HomeFamilyMemberUi

class HomeFamilyAdapter(
    private val items: List<HomeFamilyMemberUi>,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<HomeFamilyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHomeFamilyMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeFamilyMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == items.size) {
            holder.binding.avatarImageView.setImageResource(R.drawable.ic_plus_member)
            holder.binding.nameTextView.text = ""
            holder.binding.root.setOnClickListener { onAddClick() }
            return
        }

        val item = items[position]

        holder.binding.avatarImageView.setImageResource(item.avatarResId)
        holder.binding.nameTextView.text = item.name
        holder.binding.root.setOnClickListener(null)
    }
}