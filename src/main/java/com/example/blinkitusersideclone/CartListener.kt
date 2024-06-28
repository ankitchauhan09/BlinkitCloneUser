package com.example.blinkitusersideclone

interface CartListener {

    fun showCart(itemCount : Int);
    fun savingCartItemCount(itemCount: Int)
    fun hideCart()
}