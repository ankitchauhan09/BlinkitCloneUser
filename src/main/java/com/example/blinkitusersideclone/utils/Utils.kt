package com.example.blinkitusersideclone.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.blinkitusersideclone.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

object Utils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var dialog: AlertDialog? = null
    fun showDialog(context: Context, message: String) {
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.progressBarMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    private var firebaseAuthInstance: FirebaseAuth? = null
    fun getAuthInstance(): FirebaseAuth {
        if (firebaseAuthInstance == null) {
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentLoggedInUserId() : String{
        return firebaseAuthInstance?.currentUser!!.uid
    }

    fun getCurrentDate() : String {
        return Date().toString()
    }


}