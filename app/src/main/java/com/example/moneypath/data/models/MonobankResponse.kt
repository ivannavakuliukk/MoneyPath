package com.example.moneypath.data.models

import com.example.moneypath.utils.dateToUnix

data class MonoTransaction(
    val id: String,
    val amount: Int,
    val description: String,
    val mcc: Int,
    val originalMcc:Int,
    val time: Long,
    val currencyCode: Int,
    val balance: Int,
    val comment: String? = null
)

data class ClientInfo(
    val accounts: List<Account>
)

data class Account(
    val balance: Int,
    val currencyCode: Int
)

fun MonoTransaction.toTransaction(): Transaction{
    val category = findCategoryByMcc(mcc)
    val type = if(category.id == "transfer"){
        TransactionType.Transfer
    }else if(amount>0){
        TransactionType.Income
    }else TransactionType.Expense

    return Transaction(
        id = "",
        date = time,
        categoryId = category.id,
        description = description,
        amount = amount.toDouble()/100,
        type = type,
        walletId = "mono",
        walletIdTo = ""
    )
}