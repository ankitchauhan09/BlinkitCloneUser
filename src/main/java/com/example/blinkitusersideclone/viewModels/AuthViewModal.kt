package com.example.blinkitusersideclone.viewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.blinkitusersideclone.models.Users
import com.example.blinkitusersideclone.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit


class AuthViewModal : ViewModel() {
    private val TAG = "AuthViewModal"

    private val _verificationId = MutableStateFlow<String?>(null)
    private val _otpSend = MutableStateFlow<Boolean>(false)
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    private val _isCurrentUser = MutableStateFlow<Boolean>(false)

    val otpSend: MutableStateFlow<Boolean> = _otpSend
    val isLoggedIn: MutableStateFlow<Boolean> = _isLoggedIn
    val isCurrentUser = _isCurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _isCurrentUser.value = true
        }
    }


    fun sendOTP(userNumber: String, activity: Activity) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value = verificationId
                _otpSend.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91${userNumber}") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(
        otp: String,
        userNumber: String,
        activity: Activity,
    ) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            val userToken = it.result
            Utils.getAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = task?.result!!.user
                        val loggedInUser = Users(user?.uid, userNumber, null, userToken = userToken)
                        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
                            .child(loggedInUser.uid!!).setValue(loggedInUser)
                        _isLoggedIn.value = true
                    }
                }
        }
    }
}
