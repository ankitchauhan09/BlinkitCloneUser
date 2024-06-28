package com.example.blinkitusersideclone.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.activities.UserMainActivity
import com.example.blinkitusersideclone.databinding.FragmentOTPBinding
import com.example.blinkitusersideclone.utils.Utils
import com.example.blinkitusersideclone.viewModels.AuthViewModal
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {

    lateinit var binding: FragmentOTPBinding
    lateinit var userNumber: String
    private val viewModel: AuthViewModal by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(inflater)



        getUserNumber()
        customizeOtpFields()
        onBackButtonClicked()
        sendOTP()
        onLoginButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.otpLoginBtn.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you..")
            val otpFields = arrayOf(
                binding.otdNum1,
                binding.otpNum2,
                binding.otpNum3,
                binding.optNum4,
                binding.otpNum5,
                binding.otpNum6
            )
            val otp = otpFields.joinToString("") {
                it.text.toString()
            }

            if(otp.length < otpFields.size) {
                Utils.showToast(requireContext(), "Please enter a valid otp")
            } else {
                otpFields.forEach {
                    it.text?.clear()
                    it.clearFocus()
                }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        viewModel.signInWithPhoneAuthCredential(otp, userNumber, requireActivity())
        lifecycleScope.launch {
            viewModel.isLoggedIn.collect{
                Utils.hideDialog()
                if(it) {
                    Utils.showToast(requireContext(), "Logged in successfully..")
                    startActivity(Intent(requireActivity(), UserMainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Utils.showToast(requireContext(), "Unable to login..")
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP")
        viewModel.sendOTP(userNumber, requireActivity())
        lifecycleScope.launch {
            viewModel.otpSend.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "OTP sent successfully..")
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        binding.otpFragmentToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signinFragment)
        }
    }

    private fun customizeOtpFields() {

        val otpFields = arrayOf(
            binding.otdNum1,
            binding.otpNum2,
            binding.otpNum3,
            binding.optNum4,
            binding.otpNum5,
            binding.otpNum6
        )

        for (i in otpFields.indices) {

            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (i < otpFields.size - 1) {
                            otpFields[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            otpFields[i - 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

        }


    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.number.text = userNumber
    }

}