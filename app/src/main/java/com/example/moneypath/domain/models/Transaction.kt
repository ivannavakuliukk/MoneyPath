package com.example.moneypath.domain.models


enum class TransactionType{
    Income, Expense, Transfer
}

data class Transaction (
    val id: String,
    val date: Long,
    val categoryId: String,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val walletId: String,
    val walletIdTo: String,

    val amountEnc: String,
    val amountIv: String
)

