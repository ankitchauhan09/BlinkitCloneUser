package com.example.blinkitusersideclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.AdapterCartProduct
import com.example.blinkitusersideclone.databinding.FragmentOrderDetailBinding
import com.example.blinkitusersideclone.viewModels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private var status: Int = 0
    private var orderId = ""
    private lateinit var viewModel: UserViewModel
    private lateinit var orderProductAdapter : AdapterCartProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        viewModel = UserViewModel(requireActivity().application)

        getBundleValues()
        settinStatus()
        getOrderedProducts()
        onBackButtonClicked()
        return binding.root
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetail.setNavigationOnClickListener {
//            findNavController().navigate(R.id.actionorde)
        }
    }

    private fun getOrderedProducts() {
        lifecycleScope.launch {
            viewModel.getOrderedProducts(orderId).collect{
                orderProductAdapter = AdapterCartProduct()
                binding.rvOrderProducts.adapter = orderProductAdapter
                orderProductAdapter.differ.submitList(it)
            }
        }
    }

    private fun settinStatus() {
        when(status) {

            0 -> {
                binding.statusOrdered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            1 -> {
                binding.statusOrdered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewOrderedToDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.statusDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            2 -> {
                binding.statusOrdered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewOrderedToDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.statusDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewDispatchedToReceived.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.statusReceived.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            3 -> {
                binding.statusOrdered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewOrderedToDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.statusDispatched.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewDispatchedToReceived.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.statusReceived.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.viewReceivedToDelivered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                binding.stausDelivered.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }

        }
    }

    private fun getBundleValues() {
        var bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId")!!
    }
}