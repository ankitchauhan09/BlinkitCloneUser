package com.example.blinkitusersideclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.databinding.ItemViewOrdersFragmentBinding
import com.example.blinkitusersideclone.models.OrderItems

class AdapterOrders(val context: Context,val onOrderItemViewClicked: (OrderItems) -> Unit) : RecyclerView.Adapter<AdapterOrders.AdapterOrdersViewHolder>() {

    val diffiUtil = object : DiffUtil.ItemCallback<OrderItems>() {

        override fun areItemsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer<OrderItems>(this, diffiUtil)

    inner class AdapterOrdersViewHolder(val binding: ItemViewOrdersFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterOrdersViewHolder {
        return AdapterOrdersViewHolder(
            ItemViewOrdersFragmentBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AdapterOrdersViewHolder, position: Int) {
        val order = differ.currentList.get(position)
        holder.binding.apply {
            tvOrderTitle.text = order.itemTitle
            tvOrderDate.text = order.itemDate
            tvOrderAmount.text = "₹" + order.itemPrice.toString()

            when(order.itemStatus) {
                0 -> {
                    tvOrderStatus.text = "ORDERED"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
                }
                1 -> {
                    tvOrderStatus.text = "DISPATCHED"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.yellow)
                }
                2 -> {
                    tvOrderTitle.text = "RECEIVED"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
                }
                3 -> {
                    tvOrderTitle.text = "DELIVERED"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
                }
            }
        }

        holder.itemView.setOnClickListener {
            onOrderItemViewClicked(order)
        }

    }


}