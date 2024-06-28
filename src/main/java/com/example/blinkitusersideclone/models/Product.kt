package com.example.blinkitusersideclone.models

data class Product (

    var productRandomId : String? = null,
    var productTitle : String? = null,
    var productQuantity : Int? = null,
    var productUnit : String? = null,
    var productPrice : Int? = null,
    var productStock : Int? = null,
    var productCategory : String? = null,
    var productType : String? = null,
    var itemCount : Int? = null,
    var adminUID : String? = null,
    var productImageUris : ArrayList<String>? = null

)