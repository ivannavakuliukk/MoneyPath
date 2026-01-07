package com.example.moneypath.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.moneypath.R
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.models.WalletSource
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.domain.models.TransactionType

// Провайдери параметрів для Preview

// Wallets
class WalletsProvider : PreviewParameterProvider<List<Wallet>> {
    override val values: Sequence<List<Wallet>> = sequenceOf(
        listOf(
            Wallet(name = "Готівка", balance = 2405.0, id = "1"),
            Wallet(name = "mono", source = WalletSource.Api, balance = 0.0, id = "2"),
            Wallet(name = "Кредитна карта", balance = 50000.0, type = WalletType.Card, id = "3"),
            Wallet(name = "Приват", type = WalletType.Card, balance = 5.0, id = "4")
        ),
        listOf(
            Wallet( name ="Готівка", id = "1")
        ),
        emptyList(),
        listOf(
            Wallet(name = "Готівка", balance = 240500000.0),
            Wallet(name = "mono", source = WalletSource.Api, balance = 300000000.0)
        ),

    )
}

object PreviewWalletTransactionData{
    val wallets = listOf(
        Wallet(name = "Готівка", balance = 2405.0, id = "1"),
        Wallet(name = "mono", id = "2"),
    )

    val transactions = listOf(
        Transaction(
            id = "",
            date = 0L,
            categoryId = "food",
            amount = -10000.0,
            description = "Закупи на свято",
            type = TransactionType.Expense,
            walletId = "1",
            walletIdTo = "",
            amountEnc = "",
            amountIv = ""
        ),
        Transaction(
            id = "",
            date = 0L,
            categoryId = "transfer",
            amount = 500000.0,
            description = "Внутрішній",
            type = TransactionType.Transfer,
            walletId = "1",
            walletIdTo = "2",
            amountEnc = "",
            amountIv = ""
        ),
        Transaction(
            id = "",
            date = 0L,
            categoryId = "transfer",
            amount = 10.0,
            description = "Зовнішній(дохід)",
            type = TransactionType.Transfer,
            walletId = "1",
            walletIdTo = "",
            amountEnc = "",
            amountIv = ""
        ),
        Transaction(
            id = "",
            date = 0L,
            categoryId = "gift",
            amount = 1000.0,
            description = "На дн",
            type = TransactionType.Income,
            walletId = "2",
            walletIdTo = "",
            amountEnc = "",
            amountIv = ""
        )
    )
}

// list of Wallets + Transaction(1)
class WalletsAndTransactionProvider: PreviewParameterProvider<Pair<List<Wallet>, Transaction>>{
    val wallets = PreviewWalletTransactionData.wallets
    val transaction = PreviewWalletTransactionData.transactions
    override val values: Sequence<Pair<List<Wallet>, Transaction>> = sequenceOf(
        Pair(
            wallets,
            transaction[0]
        ),
        Pair(
            wallets,
            transaction[1]
        ),
        Pair(
            wallets,
            transaction[2]
        ),
        Pair(
            wallets,
          transaction[3]
        ),
    )
}

// lists of Wallets and Transactions + Boolean
class WalletsAndTransactionsProvider: PreviewParameterProvider<Triple<List<Wallet>, List<Transaction>, Boolean>>{
    val wallets = PreviewWalletTransactionData.wallets
    val transaction = PreviewWalletTransactionData.transactions
    override val values: Sequence<Triple<List<Wallet>, List<Transaction>, Boolean>> = sequenceOf(
        Triple(
            wallets,
            transaction,
            false
        ),
        Triple(
            wallets,
            emptyList(),
            false
        ),
        Triple(
            emptyList(),
            emptyList(),
            true
        )
    )
}

data class TransactionInfo(
    val icon: Int,
    val name: String,
    val amount: Double,
    val planAmount: Double?,
    val transactionSize: Int
)

class TransactionInfoProvider: PreviewParameterProvider<TransactionInfo>{
    override val values = sequenceOf(
        TransactionInfo(
            icon = R.drawable.category_beauty,
            name = "Краса/Здоров'я",
            amount = 1000.0,
            planAmount = 2000.0,
            transactionSize = 5,
        ),
        TransactionInfo(
            icon = R.drawable.category_beauty,
            name = "Краса/Здоров'я",
            amount = 200.0,
            planAmount = null,
            transactionSize = 1,
        ),
        TransactionInfo(
            icon = R.drawable.category_taxi,
            name = "Таксі",
            amount = 1000.0,
            planAmount = 700.0,
            transactionSize = 10,
        )
    )
}

class CategoriesBoxProvider: PreviewParameterProvider<Triple<Map<String, List<Transaction>>, Map<String, Double>, Boolean>>{
    override val values=sequenceOf(
        Triple(
            mapOf(
                "food" to listOf(
                    Transaction(
                        amount = 3240.0,
                        id = "",
                        date = 0L,
                        categoryId = "food",
                        description = "",
                        type = TransactionType.Expense,
                        walletId = "",
                        walletIdTo = "",
                        amountEnc = "",
                        amountIv = ""
                    )),
                "government" to listOf(Transaction(
                    amount = 5.0,
                    id = "",
                    date = 0L,
                    categoryId = "government",
                    description = "",
                    type = TransactionType.Expense,
                    walletId = "",
                    walletIdTo = "",
                    amountEnc = "",
                    amountIv = ""
                ),
                )
            ),
            second = mapOf("food" to 5000.0, "government" to 0.0),
            third = true
        ),
        Triple(
            mapOf(
                "food" to listOf(
                    Transaction(
                    amount = 3240.0,
                    id = "",
                    date = 0L,
                    categoryId = "food",
                    description = "",
                    type = TransactionType.Expense,
                    walletId = "",
                    walletIdTo = "",
                    amountEnc = "",
                    amountIv = ""
                )),
                   "government" to listOf(Transaction(
                        amount = 5.0,
                        id = "",
                        date = 0L,
                        categoryId = "government",
                        description = "",
                        type = TransactionType.Expense,
                        walletId = "",
                        walletIdTo = "",
                        amountEnc = "",
                        amountIv = ""
                    ),
                )
            ),
            second = emptyMap(),
            third = false
        ),
        Triple(
            emptyMap(),
            second = emptyMap(),
            third = false
        )
    )
}