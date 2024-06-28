package com.example.blinkitusersideclone.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProducts::class], version = 1, exportSchema = true)
abstract class CartProductsDB : RoomDatabase() {

    abstract fun cartProductDao() : CartProductsDao

    companion object{
        @Volatile
        var INSTANCE : CartProductsDB? = null
        fun getDatabaseInstance(context : Context) : CartProductsDB {
            synchronized(this){
                if (INSTANCE == null) {
                    val roomDB = Room.databaseBuilder(context, CartProductsDB::class.java, "CartProduct").build()
                    INSTANCE = roomDB
                }
                return INSTANCE!!
            }
        }
    }
}