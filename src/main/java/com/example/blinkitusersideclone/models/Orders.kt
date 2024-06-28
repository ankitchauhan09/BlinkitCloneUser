package com.example.blinkitusersideclone.models

import com.example.blinkitusersideclone.roomDB.CartProducts

data class Orders(
    val orderID : String? = null,
    val orderList : List<CartProducts>? = null,
    val userAddress : String? = null,
    val orderStatus : Int? = null,
    val orderDate : String? = null,
    val orderingUserId : String? = null
)