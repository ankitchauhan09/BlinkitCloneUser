package com.example.blinkitusersideclone.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.databinding.FragmentSigninBinding
import com.example.blinkitusersideclone.utils.Utils

class SigninFragment : Fragment() {

    lateinit var binding : FragmentSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigninBinding.inflate(inflater)

        binding.signinMobileNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var length = s?.length

                if(length == 10) {
                    binding.signinContinueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.signinContinueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lightGrey))
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.signinContinueBtn.setOnClickListener {

            val number = binding.signinMobileNumber.text.toString()

            if(number.isEmpty() || number.length != 10) {
                Utils.showToast(requireContext(), "Please enter a valid mobile number")
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signinFragment_to_OTPFragment2, bundle)
            }

        }


        return binding.root
    }


}