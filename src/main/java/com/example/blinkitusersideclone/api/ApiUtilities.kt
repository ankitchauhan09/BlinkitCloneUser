package com.example.blinkitusersideclone.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    val statusApi: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-preprod.phonepe.com/apis/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }

    val notificationApi : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }

}