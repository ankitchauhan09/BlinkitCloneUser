package com.example.blinkitusersideclone.api

import androidx.room.Dao
import com.example.blinkitusersideclone.models.CheckStatus
import com.example.blinkitusersideclone.notification.Message
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

@Dao
interface ApiInterface {

    @GET("pg-sandbox/pg/v1/status/{merchantId}/{merchantTransactionId}")
    suspend fun getStatus(
        @HeaderMap headers: Map<String, String>,
        @Path("merchantId") merchantId: String,
        @Path("merchantTransactionId") merchantTransactionId: String
    ): Response<CheckStatus>

    @POST("projects/blinkitclone-948e2/messages:send")
    fun sendNotification(
        @Header("Authorization") authHeader: String,
        @Header("Content-Type") contentType: String,
        @Body message: Message
    ): Call<Message>


}