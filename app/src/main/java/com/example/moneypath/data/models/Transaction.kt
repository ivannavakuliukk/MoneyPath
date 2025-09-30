package com.example.moneypath.data.models

import android.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.firebase.database.Exclude

enum class TransactionType{
    Income, Expense, Transfer
}

data class Transaction (
    val id: String = "",
    val date: Long = 0L,
    val categoryId: String = "",
    @get:Exclude val amount: Double = 0.0,
    val description: String = "",
    val type: TransactionType = TransactionType.Income,
    val walletId: String = "",
    val walletIdTo: String = "",

    val amountEnc: String ="",
    val amountIv: String = ""
)

