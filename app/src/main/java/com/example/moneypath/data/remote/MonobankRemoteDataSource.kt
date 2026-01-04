package com.example.moneypath.data.remote

import android.util.Log
import com.example.moneypath.data.datasource.MonobankService
import com.example.moneypath.data.remote.mappers.toDomain
import com.example.moneypath.domain.models.Account
import com.example.moneypath.domain.models.Transaction
import javax.inject.Inject

class MonobankRemoteDataSource @Inject constructor(
    private val service: MonobankService
)  {

    suspend fun getClientInfo(token: String): Account {
        Log.d("RemoteDataSource", "${service.getClientInfo(token)}")
        return service.getClientInfo(token).toDomain()
    }

    suspend fun getTransactions(
        token: String,
        fromDate: Long,
        toDate: Long
    ): List<Transaction> {
        return service.getTransactions(token, fromDate, toDate).toDomain()
    }
}
