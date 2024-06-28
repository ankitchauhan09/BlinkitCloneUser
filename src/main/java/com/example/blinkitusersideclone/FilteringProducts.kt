package com.example.blinkitusersideclone

import android.widget.Filter
import com.example.blinkitusersideclone.adapters.HomeProductViewAdapter
import com.example.blinkitusersideclone.models.Product
import java.util.Locale

class FilteringProducts(
    val adapter: HomeProductViewAdapter,
    val filter: ArrayList<Product>
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()
        if(!constraint!!.isNullOrEmpty()) {
            val filteredList = ArrayList<Product>()

            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            for (products in filter) {
                if (query.any {
                        products.productTitle.toString().uppercase(Locale.getDefault())
                            .contains(it) || products.productType.toString()
                            .uppercase(Locale.getDefault())
                            .contains(it) || products.productCategory.toString().uppercase(Locale.getDefault()).contains(it)
                    }) {
                    filteredList.add(products)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        }
        else {
            result.values = filter
            result.count  = filter.size
        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as ArrayList<Product>)
    }
}