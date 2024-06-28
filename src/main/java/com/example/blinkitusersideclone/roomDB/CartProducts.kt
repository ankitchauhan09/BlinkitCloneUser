package com.example.blinkitusersideclone.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProduct")
data class CartProducts(
    @PrimaryKey
    val productId : String = "random",
    var productTitle : String? = null,
    var productQuantity : Int? = null,
    var productUnit : String? = null,
    var productPrice : Int? = null,
    var productStock : Int? = null,
    var productCategory : String? = null,
    var productType : String? = null,
    var itemCount : Int? = null,
    var adminUID : String? = null,
    var productImage : String? = null
)