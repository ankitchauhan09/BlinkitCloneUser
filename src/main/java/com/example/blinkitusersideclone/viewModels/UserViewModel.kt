package com.example.blinkitusersideclone.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blinkitusersideclone.Constants
import com.example.blinkitusersideclone.api.ApiUtilities
import com.example.blinkitusersideclone.models.Orders
import com.example.blinkitusersideclone.models.Product
import com.example.blinkitusersideclone.models.Users
import com.example.blinkitusersideclone.notification.Message
import com.example.blinkitusersideclone.notification.MessageX
import com.example.blinkitusersideclone.notification.Notification
import com.example.blinkitusersideclone.roomDB.CartProducts
import com.example.blinkitusersideclone.roomDB.CartProductsDB
import com.example.blinkitusersideclone.roomDB.CartProductsDao
import com.example.blinkitusersideclone.utils.AccessToken
import com.example.blinkitusersideclone.utils.Utils
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileInputStream
import java.io.IOException


class UserViewModel(val context: Application) : AndroidViewModel(context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("My_Preferences", MODE_PRIVATE)

    private val _isPaymnetDone = MutableStateFlow<Boolean>(false)
    val isPaymentDone = _isPaymnetDone

    fun getAddressStatus(): MutableLiveData<Boolean> {
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status;
    }

    fun saveAddressStatus() {
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    // firebase call
    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getCategoryProduct(title: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductCategory/${title}")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = ArrayList<Product>()
                for (products in snapshot.children) {
                    val prod = products.getValue(Product::class.java)
                    productList.add(prod!!)
                }
                trySend(productList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun getOrderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmins").child("Orders")
            .child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderList = ArrayList<CartProducts>()
                    val order = snapshot.getValue(Orders::class.java)
                    trySend(order?.orderList!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun updateItemCount(product: Product) {

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("AllProducts/${product.productRandomId}").setValue(product)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductCategory/${product.productCategory}")
            .child("${product.productRandomId}").setValue(product)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductType/${product.productType}")
            .child(product.productRandomId.toString()).setValue(product)
    }

    fun saveProductAfterOrder(stock: Int, product: CartProducts) {

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("AllProducts/${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductCategory/${product.productCategory}")
            .child("${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductType/${product.productType}")
            .child(product.productId.toString()).child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("AllProducts/${product.productId}").child("productStock").setValue(stock)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductCategory/${product.productCategory}")
            .child("${product.productId}").child("productStock").setValue(stock)

        FirebaseDatabase.getInstance().getReference("AllAdmins")
            .child("ProductType/${product.productType}")
            .child(product.productId.toString()).child("productStock").setValue(stock)
    }

    fun saveUserAddress(users: Users) {
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
            .child(Utils.getCurrentLoggedInUserId()).child("userAddress")
            .setValue(users.userAddress)
    }


    fun getUserAddress(callback: (String?) -> Unit) {
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
            .child(Utils.getCurrentLoggedInUserId()).child("userAddress")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("Orders", snapshot.getValue(String::class.java).toString())
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }

        })
    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("AllAdmins").child("Orders")
            .orderByChild("orderStatus")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderList = ArrayList<Orders>()
                    for (orders in snapshot.children) {
                        val order = orders.getValue(Orders::class.java)
                        order?.let {
                            if (it.orderingUserId == Utils.getCurrentLoggedInUserId()) {
                                orderList.add(it)
                            }
                        }
                    }
                    trySend(orderList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

//cart db call

    fun savingCartItemCount(itemCount: Int) {
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    fun fetchTotalItemCount(): MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }

    //room database calls
    val cartProductDao: CartProductsDao =
        CartProductsDB.getDatabaseInstance(context).cartProductDao()

    suspend fun insertCartProducts(products: CartProducts) {
        cartProductDao.insertCartProduct(products)
    }

    suspend fun updateCartProducts(products: CartProducts) {
        cartProductDao.updateCartProduct(products)
    }

    suspend fun deleteCartProducts(productId: String) {
        cartProductDao.deleteCartProduct(productId)
    }

    fun getAll(): LiveData<List<CartProducts>> {
        return cartProductDao.getAllCartProducts()
    }

    fun deleteCartProducts() {
        cartProductDao.deleteCartProducts()
    }

    //retrofit methods
    suspend fun checkPayment(headers: Map<String, String>) {
        val res = ApiUtilities.statusApi.getStatus(
            headers,
            Constants.MERCHANT_ID,
            Constants.MERCHANT_TRANSACTION_ID
        )
        _isPaymnetDone.value = res.body() != null && res.body()!!.success
    }

    fun saveOrderedProduct(orders: Orders) {
        FirebaseDatabase.getInstance().getReference("AllAdmins").child("Orders")
            .child(orders.orderID.toString()).setValue(orders)
    }

    @Throws(IOException::class)
    private fun getAccessToken(): String {
        val googleCredentials: GoogleCredentials = GoogleCredentials
            .fromStream(FileInputStream("assets/service_file.json"))
        googleCredentials.refreshIfExpired()
        return googleCredentials.getAccessToken().getTokenValue()
    }

    suspend fun sendNotification(adminId: String, title: String, body: String) {
        Log.d("metho", "noti metho runn")
        val getToken = FirebaseDatabase.getInstance().getReference().child("AllAdmins")
            .child("Admins")
            .child(adminId)
            .child("adminToken")
            .get()
        getToken.addOnCompleteListener {
            viewModelScope.launch {
                val token = getToken.result.getValue(String::class.java)
                val message = Message(MessageX(Notification(body, title), token!!))
                Log.d("metho", token.toString())
                Log.d("Notification", "Authorization header: Bearer + ${AccessToken().getAccessToken()}")
                Log.d("Notification", "Message payload: $message")

                ApiUtilities.notificationApi.sendNotification(
                    "Bearer ${AccessToken().getAccessToken()}",
                    "application/json",
                    message
                ).enqueue(object : Callback<Message> {
                    override fun onResponse(p0: Call<Message>, p1: Response<Message>) {
                        if (p1.isSuccessful) {
                            Log.d("metho", "succ $p1")
                            Utils.showToast(context, "Notification Sent Successfully")
                        } else {
                            Log.d("metho", "fail $p1")
                            Utils.showToast(context, "N otification failed Successfully")
                        }
                    }

                    override fun onFailure(p0: Call<Message>, p1: Throwable) {
                        Log.d("metho", "fail $p1")
                        Utils.showToast(context, "Notification failed Successfully")
                    }

                })
            }
        }
    }
}