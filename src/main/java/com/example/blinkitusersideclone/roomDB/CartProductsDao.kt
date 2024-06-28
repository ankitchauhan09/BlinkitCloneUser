package com.example.blinkitusersideclone.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductsDao {

    @Insert
    fun insertCartProduct(products: CartProducts)

    @Update
    fun updateCartProduct(products: CartProducts)

    @Query("SELECT * FROM CartProduct")
    fun getAllCartProducts() : LiveData<List<CartProducts>>

    @Query("DELETE FROM CartProduct where productId = :productId")
    fun deleteCartProduct(productId : String)

    @Query("DELETE FROM CartProduct")
    fun deleteCartProducts()
}