package com.example.blinkitusersideclone.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blinkituserclone.modals.Category
import com.example.blinkitusersideclone.Constants
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.AdapterCategory
import com.example.blinkitusersideclone.databinding.FragmentHomeBinding
import com.example.blinkitusersideclone.viewModels.UserViewModel


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        viewModel = UserViewModel(requireActivity().application)

        setStatusBarColor()
        setAllCategories()
        navigateToSearchFragment()
        getAllProductsFromCart()
        onProfileButtonClick()
        return binding.root
    }

    private fun onProfileButtonClick() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun getAllProductsFromCart() {
        viewModel.getAll().observe(viewLifecycleOwner) {
            for(i in it) {
                Log.d("LOLA", i.productTitle.toString())
                Log.d("LOLA", i.itemCount.toString())
            }
        }
    }

    private fun navigateToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onCategoryItemClicked(category: Category) {
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    private fun setAllCategories() {
        val categoryList = Constants.categoryList;
        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryItemClicked)

    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(context, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}