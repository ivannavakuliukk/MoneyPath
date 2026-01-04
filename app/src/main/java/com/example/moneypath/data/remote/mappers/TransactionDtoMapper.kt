package com.example.moneypath.data.remote.mappers

import com.example.moneypath.data.remote.models.TransactionDto
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.domain.models.TransactionType

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        date = date,
        categoryId = categoryId,
        amount = 0.0,
        description = description,
        type = when(type.lowercase()){
            "transfer" -> TransactionType.Transfer
            "expense" -> TransactionType.Expense
            "income" -> TransactionType.Income
            else -> TransactionType.Transfer
        },
        walletId = walletId,
        walletIdTo = walletIdTo,
        amountEnc = amountEnc,
        amountIv = amountIv,
    )
}

fun List<TransactionDto>.toDomain(): List<Transaction>{
    return this.map { transaction-> transaction.toDomain() }
}

fun Transaction.toData(): TransactionDto {
    return TransactionDto(
        id = id,
        date = date,
        categoryId = categoryId,
        description = description,
        type = type.name,
        walletId = walletId,
        walletIdTo = walletIdTo,
        amountEnc = amountEnc,
        amountIv = amountIv
    )
}


fun List<Transaction>.toData():List<TransactionDto>{
    return this.map{transaction -> transaction.toData()}
}