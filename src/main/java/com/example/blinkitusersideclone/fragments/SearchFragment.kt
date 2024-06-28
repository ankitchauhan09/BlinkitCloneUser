package com.example.blinkitusersideclone.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.CartListener
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.HomeProductViewAdapter
import com.example.blinkitusersideclone.databinding.FragmentSearchBinding
import com.example.blinkitusersideclone.databinding.RvProductViewBinding
import com.example.blinkitusersideclone.models.Product
import com.example.blinkitusersideclone.roomDB.CartProducts
import com.example.blinkitusersideclone.utils.Utils
import com.example.blinkitusersideclone.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var productViewAdapter: HomeProductViewAdapter
    private var cartListener: CartListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        viewModel = UserViewModel(requireActivity().application)

        getAllProducts()
        navigationToSearchFragment()
        backToHomeFragment()
        searchProduct()

        return binding.root
    }

    private fun backToHomeFragment() {
        binding.backToHomeFragment.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun navigationToSearchFragment() {
        binding.searchFragmentCV.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun searchProduct() {
        binding.searchFragmentSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                productViewAdapter.filter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun getAllProducts() {
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts().collect {
                if (it.isEmpty()) {
                    binding.searchFragmentRvProducts.visibility = View.GONE
                    binding.noProductFoundTxt.visibility = View.VISIBLE
                } else {
                    binding.searchFragmentRvProducts.visibility = View.VISIBLE
                    binding.noProductFoundTxt.visibility = View.GONE

                    productViewAdapter = HomeProductViewAdapter(
                        ::onAddButtonClicked,
                        ::onIncrementButtonClick,
                        ::onDecrementButtonClick
                    )
                    binding.searchFragmentRvProducts.adapter = productViewAdapter
                    productViewAdapter.differ.submitList(it)
                    productViewAdapter.originalList = it as ArrayList<Product>
                }
            }
        }
    }

    fun onAddButtonClicked(product: Product, productBinding: RvProductViewBinding) {
        productBinding.rvProductViewProductEditBtn.visibility = View.GONE
        productBinding.productViewProductCount.visibility = View.VISIBLE

        //step 1 : update views
        var itemCount = productBinding.productCartCountValue.text.toString().toInt()
        itemCount++
        productBinding.productCartCountValue.text = itemCount.toString()
        product.itemCount = itemCount
        cartListener?.showCart(1)

        //step 2 :
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDB(product)
            viewModel.updateItemCount(product)
        }
    }

    fun onIncrementButtonClick(product: Product, productBinding: RvProductViewBinding) {
        var itemCount = productBinding.productCartCountValue.text.toString().toInt()
        itemCount++

        if (product.productStock!! + 1 > itemCount) {
            productBinding.productCartCountValue.text = itemCount.toString()
            product.itemCount = itemCount
            cartListener?.showCart(1)

            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                updateProductInRoomDB(product)
                viewModel.updateItemCount(product)
            }
        } else {
            Utils.showToast(requireContext(), "Maximum limit reached for this product...")
        }

    }

    fun onDecrementButtonClick(product: Product, productBinding: RvProductViewBinding) {

        var itemCount = productBinding.productCartCountValue.text.toString().toInt()
        itemCount--

        if (itemCount > 0) {
            productBinding.productCartCountValue.text = itemCount.toString()
            product.itemCount = itemCount
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(-1)
                updateProductInRoomDB(product)
                viewModel.updateItemCount(product)
            }
        } else {
            productBinding.rvProductViewProductEditBtn.visibility = View.VISIBLE
            productBinding.productViewProductCount.visibility = View.GONE
            productBinding.productCartCountValue.text = "0"
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.deleteCartProducts(product.productRandomId!!)
            }
        }
        cartListener?.showCart(-1)


    }

    private fun saveProductInRoomDB(product: Product) {
        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle!!,
            productQuantity = product.productQuantity,
            productPrice = product.productPrice,
            productUnit = product.productUnit,
            itemCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris!!.get(0),
            productCategory = product.productCategory,
            adminUID = product.adminUID
        )
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.insertCartProducts(cartProduct)
        }
    }

    private fun updateProductInRoomDB(product: Product) {
        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle!!,
            productQuantity = product.productQuantity,
            productPrice = product.productPrice,
            productUnit = product.productUnit,
            itemCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris!!.get(0),
            productCategory = product.productCategory,
            adminUID = product.adminUID
        )
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.updateCartProducts(cartProduct)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("Please implement cart listener")
        }
    }

}