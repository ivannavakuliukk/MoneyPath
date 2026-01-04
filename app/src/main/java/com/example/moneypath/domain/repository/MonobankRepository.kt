package com.example.moneypath.domain.repository

import com.example.moneypath.domain.models.Account
import com.example.moneypath.domain.models.Transaction

interface MonobankRepository {
    fun saveToken(token: String)
    fun hasToken(): Boolean
    fun clearToken()
    suspend fun isTokenValid(): Boolean
    suspend fun getTransactionsBetween(from: Long, to: Long): List<Transaction>
    suspend fun getAccountInfo(): Account?
}
