package com.example.moneypath.data.remote.mappers

import com.example.moneypath.data.models.findCategoryByMcc
import com.example.moneypath.data.remote.models.TransactionMonoDto
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.domain.models.TransactionType

fun TransactionMonoDto.toDomain(): Transaction{
    val category = findCategoryByMcc(mcc)
    val type = if(category.id == "transfer"){
        TransactionType.Transfer
    }else if(amount>0){
        TransactionType.Income
    }else TransactionType.Expense

    return Transaction(
        id = "",
        date = date,
        categoryId = category.id,
        description = description,
        amount = amount.toDouble() / 100,
        type = type,
        walletId = "mono",
        walletIdTo = "",
        amountEnc = "",
        amountIv = ""
    )
}

fun List<TransactionMonoDto>.toDomain():List<Transaction>{
    return this.map {transaction -> transaction.toDomain() }
}