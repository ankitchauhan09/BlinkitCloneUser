package com.example.blinkitusersideclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkitusersideclone.FilteringProducts
import com.example.blinkitusersideclone.databinding.RvProductViewBinding
import com.example.blinkitusersideclone.models.Product

class HomeProductViewAdapter(
    val onAddButtonClicked: (Product, RvProductViewBinding) -> Unit,
    val onIncrementButtonClicked: (Product, RvProductViewBinding) -> Unit,
    val onDecrementButtonClicked: (Product, RvProductViewBinding) -> Unit
) :
    RecyclerView.Adapter<HomeProductViewAdapter.HomeProductViewHolder>(), Filterable{

    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem;
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class HomeProductViewHolder(val binding: RvProductViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeProductViewHolder {
        return HomeProductViewHolder(
            RvProductViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: HomeProductViewHolder,
        position: Int
    ) {
        val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            val productImage = product.productImageUris
            productImage?.forEach {
                imageList.add(SlideModel(it))
            }

            rvProductViewImageSlider.setImageList(imageList)

            rvProductViewProductName.text = product.productTitle
            rvProductViewProductQuantity.text =
                (product.productQuantity.toString() + product.productUnit.toString())
            rvProductViewProductPrice.text = "₹" + product.productPrice.toString()

            if(product.itemCount!! > 0 ) {
                productCartCountValue.text = product.itemCount.toString()
                rvProductViewProductEditBtn.visibility = View.GONE
                productViewProductCount.visibility = View.VISIBLE
            }

            rvProductViewProductEditBtn.setOnClickListener{
                onAddButtonClicked(product, this)
            }

            productCartCountIncrementer.setOnClickListener{
                onIncrementButtonClicked(product, this)
            }

            productCartCountDecrementer.setOnClickListener {
                onDecrementButtonClicked(product, this)
            }
        }
//
//        holder.itemView.setOnClickListener {
//            showEditProductDialog(product)
//        }
    }


    private val filter: FilteringProducts? = null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filter == null) {
            return FilteringProducts(this, originalList)
        }
        return filter
    }

}