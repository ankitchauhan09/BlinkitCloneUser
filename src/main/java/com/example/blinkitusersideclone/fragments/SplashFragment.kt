package com.example.blinkitusersideclone.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.activities.UserMainActivity
import com.example.blinkitusersideclone.databinding.FragmentSplashBinding
import com.example.blinkitusersideclone.viewModels.AuthViewModal
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: AuthViewModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater)
        viewModel = AuthViewModal()
        setStatusBarColor()
        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch {
                viewModel.isCurrentUser.collect {
                    if (it) {
                        startActivity(Intent(requireActivity(), UserMainActivity::class.java))
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_signinFragment2)
                    }
                }
            }
        }, 5000)

        return binding.root
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