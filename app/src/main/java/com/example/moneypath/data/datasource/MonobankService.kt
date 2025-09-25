package com.example.moneypath.data.datasource

import com.example.moneypath.data.models.ClientInfo
import com.example.moneypath.data.models.MonoTransaction
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MonobankService {
    @GET("personal/statement/0/{from}/{to}")
    suspend fun getTransactions(
        @Header("X-Token") token: String,
        @Path("from") from: Long,
        @Path("to") to: Long
        ): List<MonoTransaction>

    @GET("personal/client-info")
    suspend fun getClientInfo(
        @Header("X-Token") token: String
    ): ClientInfo
}
