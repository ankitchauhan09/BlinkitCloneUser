package com.example.blinkitusersideclone.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blinkitusersideclone.CartListener
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.AdapterCartProduct
import com.example.blinkitusersideclone.databinding.ActivityMainBinding
import com.example.blinkitusersideclone.databinding.BottomSheetCartProductsBinding
import com.example.blinkitusersideclone.roomDB.CartProducts
import com.example.blinkitusersideclone.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UserMainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapter: AdapterCartProduct

    private fun onNextButtonClicked() {
        binding.homeFragmentCartNextButton.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = UserViewModel(application)

        getTotalItemCount()
        getAllProductsFromCart()
        onCartClick()
        onNextButtonClicked()
    }

    private fun onCartClick() {
        binding.llItemCart.setOnClickListener {
            val bottomSheetCartProductsBinding = BottomSheetCartProductsBinding.inflate(
                LayoutInflater.from(this)
            )

            val bs = BottomSheetDialog(this)
            bs.setContentView(bottomSheetCartProductsBinding.root)

            bottomSheetCartProductsBinding.btProductCount.text = binding.cartLayoutProductCount.text
            adapter = AdapterCartProduct()
            bottomSheetCartProductsBinding.rvBottomSheetCartItems.adapter = adapter
            adapter.differ.submitList(cartProductList)
            bs.show()
        }
    }

    private fun getAllProductsFromCart() {
        viewModel.getAll().observe(this) {
            cartProductList = it

        }
    }


    private fun getTotalItemCount() {
        viewModel.fetchTotalItemCount().observe(this) {
            if (it > 0) {
                showCart(it)
            }
        }
    }

    override fun showCart(itemCount: Int) {
        var previousCount = binding.cartLayoutProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.cartLayoutProductCount.text = updatedCount.toString()
            binding.llItemCartMain.visibility = View.VISIBLE
        } else {
            binding.llItemCartMain.visibility = View.GONE
            binding.cartLayoutProductCount.text = "0"
        }

    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalItemCount().observe(this) {
            viewModel.savingCartItemCount(it + itemCount)
        }
    }

    override fun hideCart() {
        binding.llItemCartMain.visibility = View.GONE
        binding.cartLayoutProductCount.text = "0"
    }
}