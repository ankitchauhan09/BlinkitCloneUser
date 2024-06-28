package com.example.blinkitusersideclone.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkitusersideclone.CartListener
import com.example.blinkitusersideclone.Constants
import com.example.blinkitusersideclone.R
import com.example.blinkitusersideclone.adapters.AdapterCartProduct
import com.example.blinkitusersideclone.databinding.ActivityOrderPlaceBinding
import com.example.blinkitusersideclone.databinding.AddressLayoutBinding
import com.example.blinkitusersideclone.models.Orders
import com.example.blinkitusersideclone.models.Users
import com.example.blinkitusersideclone.roomDB.CartProducts
import com.example.blinkitusersideclone.utils.Utils
import com.example.blinkitusersideclone.viewModels.UserViewModel
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest

class OrderPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderPlaceBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapter: AdapterCartProduct
    private lateinit var b2BPGRequest: B2BPGRequest
    private var cartListener: CartListener? = null

    private val B2B_PG_REQUEST_CODE = 777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = UserViewModel(application)

        getAllCardProducts()
        onNavigationBackButtonClicked()
        onPlaceOrderClicked()
        initializePhonePay()
    }

    private fun initializePhonePay() {

        PhonePe.init(this@OrderPlaceActivity, PhonePeEnvironment.SANDBOX, Constants.MERCHANT_ID, "")

        val payload_object = JSONObject()

        payload_object.put("merchantId", Constants.MERCHANT_ID)
        payload_object.put("merchantTransactionId", Constants.MERCHANT_TRANSACTION_ID)
        payload_object.put("merchantUserId", Constants.MERCHANT_USER_ID)
        payload_object.put("amount", 1000000)
        payload_object.put("callbackUrl", "https://webhook.site/callback-url")
        payload_object.put("mobileNumber", "9999999999")

        val paymentInstrument = JSONObject()
        paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("targetApp", "com.phonepe.simulator")

        val deviceContext = JSONObject()
        deviceContext.put("deviceOS", "ANDROID")

        payload_object.put("paymentInstrument", paymentInstrument)
        payload_object.put("deviceContext", deviceContext)

        val payloadBase64 = Base64.encodeToString(
            payload_object.toString().toByteArray(Charset.defaultCharset()), Base64.NO_WRAP
        )

        Log.d("PhonePe", "Initializing Phonepe")
        try {
            PhonePe.setFlowId("UniqueFlowId") // Optional, but recommended
            val upiApps = PhonePe.getUpiApps()
            for (app in upiApps) {
                Log.d("PhonePe", "UPI App: ${app.applicationName} (${app.packageName})")
            }
        } catch (e: PhonePeInitException) {
            Log.e("PhonePe", "Error getting UPI apps: ${e.message}")
        }

        val checkSum =
            sha256(payloadBase64 + Constants.API_END_POINT + Constants.SALT_KEY) + "###${Constants.SALT_KEY_INDEX}"

        b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checkSum)
            .setUrl(Constants.API_END_POINT)
            .build()

        Log.d("PhonePe", "Payload: $payload_object")
        Log.d("PhonePe", "Payload Base64: $payloadBase64")
        Log.d("PhonePe", "Checksum: $checkSum")

    }

    private fun sha256(checkSumString: String): String {
        val bytes = checkSumString.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }


    private fun onPlaceOrderClicked() {
        binding.checkoutNextButton.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status) {
                    startPayment()
                } else {
                    val addressLayout = AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this).setView(addressLayout.root).create()
                    alertDialog.show()
                    addressLayout.saveAddressBtn.setOnClickListener {
                        saveAddress(alertDialog, addressLayout)
                    }

                }
            }
        }
    }

    private val phonePeImplicitIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getPaymentStatus()
            } else {
                Log.e(
                    "PhonePe",
                    "Payment failed or cancelled with result code: ${result.resultCode}"
                )
                if (result.data != null) {
                    Log.d("PhonePe", "Result data: ${result.data!!.extras?.toString()}")
                    for (key in result.data!!.extras?.keySet() ?: emptySet()) {
                        Log.d("PhonePe", "Result data[$key] = ${result.data?.extras?.get(key)}")
                    }
                }
                Utils.showToast(this@OrderPlaceActivity, "Payment failed or cancelled")
            }
        }


    private fun getPaymentStatus() {

        Log.d("PhonePe", "Getting payment status")

        val x_verify =
            sha256("/pg/v1/status/${Constants.MERCHANT_ID}/${Constants.MERCHANT_TRANSACTION_ID}" + Constants.SALT_KEY) + "###" + Constants.SALT_KEY_INDEX

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to x_verify,
            "X-MERCHANT-ID" to Constants.MERCHANT_ID
        )

        lifecycleScope.launch {
            viewModel.checkPayment(headers)
            viewModel.isPaymentDone.collect { status ->
                if (status) {
                    Utils.showToast(this@OrderPlaceActivity, "Payment done")
                    saveOrder()
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.deleteCartProducts()
                        viewModel.savingCartItemCount(0)
                        cartListener?.hideCart()
                    }
                } else {
                    Utils.showToast(this@OrderPlaceActivity, "Payment failed!!")
                }
            }
        }
    }

    fun generateRandomId(length: Int): String {
        val allowedCharacters = ('0'..'9') + ('a'..'z') + ('A'..'Z')
        return (1..length)
            .map { allowedCharacters.random() }
            .joinToString("")
    }


    private fun saveOrder() {
        viewModel.getAll().observe(this@OrderPlaceActivity) {
            if (!it.isNullOrEmpty()) {
                viewModel.getUserAddress { address ->
                    if (!address.isNullOrEmpty()) {

                        val orders = Orders(
                            orderID = generateRandomId(10),
                            orderList = it,
                            userAddress = address,
                            orderStatus = 0,
                            orderDate = Utils.getCurrentDate(),
                            orderingUserId = Utils.getCurrentLoggedInUserId(),
                        )

                        viewModel.saveOrderedProduct(orders)
                        Utils.hideDialog()
                        startActivity(Intent(this@OrderPlaceActivity, UserMainActivity::class.java))
                        finish()
                    }
                }
                var orderTitless = StringBuilder()
                for (products in it) {
                    orderTitless.append("${products.productTitle},")
                    val count = products.itemCount
                    val stock = products.productStock?.minus(count!!)
                    if (stock != null) {
                        viewModel.saveProductAfterOrder(stock, products)
                    }
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    Log.d("metho", it[0].adminUID.toString())
                    viewModel.sendNotification(
                        it[0].adminUID!!,
                        "Order Successfull",
                        "$orderTitless"
                    )
                }
            }
        }
    }

    private fun startPayment() {
        try {
            val intent = PhonePe.getImplicitIntent(
                this@OrderPlaceActivity,
                b2BPGRequest,
                "com.phonepe.simulator"
            )
            if (intent != null) {
                val packageManager = packageManager
                if (intent.resolveActivity(packageManager) != null) {
                    Log.d("PhonePe", "Payment intent resolved, starting payment")
                    phonePeImplicitIntentLauncher.launch(intent)
                } else {
                    Log.e("PhonePe", "Payment intent could not be resolved")
                    Utils.showToast(
                        this@OrderPlaceActivity,
                        "PhonePe app not found or intent could not be resolved"
                    )
                }
            } else {
                Log.e("PhonePe", "PhonePe app not found or intent is null")
                Utils.showToast(this@OrderPlaceActivity, "PhonePe app not found or intent is null")
            }
        } catch (e: Exception) {
            Log.e("PhonePe", "Failed to launch PhonePe payment: ${e.message}")
            Utils.showToast(
                this@OrderPlaceActivity,
                "Failed to launch PhonePe payment: ${e.message}"
            )
        }
    }


    private fun saveAddress(alertDialog: AlertDialog?, addressLayout: AddressLayoutBinding) {
        Utils.showDialog(this, "Processing")
        val userPincode = addressLayout.addressPinCode.text.toString()
        val userPhoneNo = addressLayout.addressPhoneNo.text.toString()
        val userDistrict = addressLayout.addressDistrict.text.toString()
        val userLocalityAndStreet = addressLayout.addressStreetAndLocality.text.toString()
        val userState = addressLayout.addressState.text.toString()

        val address =
            "${userLocalityAndStreet}, ${userDistrict}, ${userState}, - ${userPincode}, Contac - ${userPhoneNo}"
        val users = Users(
            userAddress = address
        )
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.saveUserAddress(users)
            viewModel.saveAddressStatus()
        }
        Utils.showToast(this, "Address saved successfully...")
        alertDialog?.dismiss()
        Utils.hideDialog()
    }

    private fun onNavigationBackButtonClicked() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UserMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCardProducts() {
        viewModel.getAll().observe(this) {
            adapter = AdapterCartProduct()
            binding.rvProductsItems.adapter = adapter
            adapter.differ.submitList(it)

            var totalPrice = 0
            var deliveryCharges = 0
            var grandTotal = 0

            for (products in it) {
                val price = products.productPrice!!
                val itemCount = products.itemCount!!
                totalPrice += (price * itemCount)
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice < 100) {
                deliveryCharges = 40
                binding.deliveryCharges.text = deliveryCharges.toString()
            } else {
                binding.deliveryCharges.text = "Free"
            }

            grandTotal = deliveryCharges + totalPrice
            binding.tvGrandTotal.text = grandTotal.toString()
        }
    }
}