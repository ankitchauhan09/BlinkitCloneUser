package com.example.blinkitusersideclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkitusersideclone.databinding.ItemViewCartBinding
import com.example.blinkitusersideclone.roomDB.CartProducts

class AdapterCartProduct : RecyclerView.Adapter<AdapterCartProduct.AdapterCartProductHolder>() {

    inner class AdapterCartProductHolder(val binding: ItemViewCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    val diffUtil = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer<CartProducts>(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCartProductHolder {
        return AdapterCartProductHolder(
            ItemViewCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AdapterCartProductHolder, position: Int) {
        val product = differ.currentList.get(position)

        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivProductImage)
            ivProductTitle.text = product.productTitle
            tvProductQuantity.text = product.productQuantity.toString()
            tvProductPrice.text = "₹" + product.productPrice.toString()
            tvProductCount.text = product.itemCount.toString()
        }
    }

}