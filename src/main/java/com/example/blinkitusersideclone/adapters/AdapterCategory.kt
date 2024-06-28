package com.example.blinkitusersideclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkituserclone.modals.Category
import com.example.blinkitusersideclone.databinding.ItemViewProductCategoryBinding

class AdapterCategory(
    val categoryList: ArrayList<Category>,
    val onCategoryItemClicked : (Category) -> Unit,
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemViewProductCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemViewProductCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryProductName.text = category.title
        }

        holder.itemView.setOnClickListener {
            onCategoryItemClicked(category)
        }
    }

}