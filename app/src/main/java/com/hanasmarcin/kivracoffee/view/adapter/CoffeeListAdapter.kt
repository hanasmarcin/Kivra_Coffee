package com.hanasmarcin.kivracoffee.view.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hanasmarcin.kivracoffee.databinding.ItemCoffeeBinding
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.utils.TRANSITION_NAME_PATTERN

class CoffeeListAdapter(
    private val getFlagsForCountryName: (String) -> BitmapDrawable?,
    private val onItemClick: (CoffeeModel, View) -> Unit
) :
    ListAdapter<CoffeeModel, CoffeeListAdapter.CoffeeListAdapterViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeListAdapterViewHolder {
        val binding = ItemCoffeeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CoffeeListAdapterViewHolder(binding, getFlagsForCountryName, onItemClick)
    }

    override fun onBindViewHolder(holder: CoffeeListAdapterViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        object DIFF_CALLBACK : DiffUtil.ItemCallback<CoffeeModel>() {
            override fun areItemsTheSame(oldItem: CoffeeModel, newItem: CoffeeModel) =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: CoffeeModel, newItem: CoffeeModel) =
                oldItem == newItem
        }
    }

    inner class CoffeeListAdapterViewHolder(
        private val binding: ItemCoffeeBinding,
        private val getFlagsForCountryName: (String) -> BitmapDrawable?,
        private val onItemClick: (CoffeeModel, View) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CoffeeModel) = with(binding) {
            tvTitle.text = item.blendName
            tvVariety.text = item.variety
            tvOrigin.text = item.intensifier
            coffeeBagContent.apply {
                cvFlag.rotation = item.flagRotation
                root.transitionName = TRANSITION_NAME_PATTERN.format(item.uid)
                ivFlag.setImageDrawable(getFlagsForCountryName(item.country))
            }
            root.setOnClickListener { onItemClick(item, this.coffeeBagContent.root) }
        }
    }
}