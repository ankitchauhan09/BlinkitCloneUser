package com.example.blinkitusersideclone.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.CartListener
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.HomeProductViewAdapter
import com.example.blinkitusersideclone.databinding.FragmentCategoryBinding
import com.example.blinkitusersideclone.databinding.RvProductViewBinding
import com.example.blinkitusersideclone.models.Product
import com.example.blinkitusersideclone.roomDB.CartProducts
import com.example.blinkitusersideclone.utils.Utils
import com.example.blinkitusersideclone.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private var categoryTitle: String? = null
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
        binding = FragmentCategoryBinding.inflate(inflater)
        viewModel = UserViewModel(requireActivity().application)
        productViewAdapter = HomeProductViewAdapter(
            ::onAddButtonClicked,
            ::onIncrementButtonClick,
            ::onDecrementButtonClick
        )

        getProductCategory()
        setToolbarTitle()
        fetchCategoryProducts()
        onSearchMenuClick()
        onNavigationItemClicked()
        return binding.root
    }

    private fun onNavigationItemClicked() {
        binding.backToHomeFragment.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClick() {
        binding.categoryFragmentHeaderToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun fetchCategoryProducts() {
        lifecycleScope.launch {
            viewModel.getCategoryProduct(categoryTitle!!).collect {
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

    private fun setToolbarTitle() {
        binding.categoryFragmentToolbarCategoryName.text = categoryTitle
    }

    private fun getProductCategory() {
        val bundle = arguments
        categoryTitle = bundle?.getString("category")
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
            productType = product.productType,
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
            productType = product.productType,
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