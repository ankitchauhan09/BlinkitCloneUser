package com.example.blinkitusersideclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.AdapterOrders
import com.example.blinkitusersideclone.databinding.FragmentOrderBinding
import com.example.blinkitusersideclone.models.OrderItems
import com.example.blinkitusersideclone.viewModels.UserViewModel
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapterOrders : AdapterOrders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater)
        viewModel = UserViewModel(requireActivity().application)

        onBackButtonClicked()
        getAllOrders()
        return binding.root
    }

    private fun getAllOrders() {

        binding.shimmerViewContainer.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getAllOrders().collect { orderList ->
                if (orderList.isNotEmpty()) {
                    val orderedItemList = ArrayList<OrderItems>()
                    for (orders in orderList) {
                        val title = StringBuilder()
                        var totalPrice = 0
                        for (products in orders.orderList!!) {
                            val price = products.productPrice!!
                            val itemCount = products.itemCount!!
                            totalPrice += (price * itemCount)

                            title.append("${products.productTitle}, ")
                        }

                        val orderedItem = OrderItems(
                            orderId = orders.orderID,
                            itemDate = orders.orderDate,
                            itemStatus = orders.orderStatus,
                            itemTitle = title.toString(),
                            itemPrice = totalPrice
                        )

                        orderedItemList.add(orderedItem)
                    }

                    adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedItemList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun onOrderItemViewClicked(order : OrderItems){
        val bundle = Bundle()
        bundle.putInt("status", order.itemStatus!!)
        bundle.putString("orderId", order.orderId)
        findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle)
    }


    private fun onBackButtonClicked() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_profileFragment)
        }
    }

}