package com.example.moneypath.data.datasource
import com.example.moneypath.data.remote.models.ClientInfoDto
import com.example.moneypath.data.remote.models.TransactionMonoDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MonobankService {
    @GET("personal/statement/0/{from}/{to}")
    suspend fun getTransactions(
        @Header("X-Token") token: String,
        @Path("from") from: Long,
        @Path("to") to: Long
        ): List<TransactionMonoDto>

    @GET("personal/client-info")
    suspend fun getClientInfo(
        @Header("X-Token") token: String
    ): ClientInfoDto
}
