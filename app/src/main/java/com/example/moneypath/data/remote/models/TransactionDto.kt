package com.example.moneypath.data.remote.models

import com.example.moneypath.domain.models.TransactionType

data class TransactionDto (
    val id: String = "",
    val date: Long = 0L,
    val categoryId: String ="",
    val description: String = "",
    val type: String = "",
    val walletId: String ="",
    val walletIdTo: String = "",

    val amountEnc: String = "",
    val amountIv: String = ""
)