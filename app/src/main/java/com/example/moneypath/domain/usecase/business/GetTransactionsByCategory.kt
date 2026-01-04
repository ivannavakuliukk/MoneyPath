package com.example.moneypath.domain.usecase.business

import android.util.Log
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.domain.models.TransactionType
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class GetTransactionsByCategories @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val cryptoRepository: CryptoRepository
)
{
    sealed class Result {
        data class Success(
            val data: Map<String, List<Transaction>>,
            val totalExpenses: Double,
            val totalIncome: Double
        ): Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(categoryList: List<String>, dates: Pair<Long, Long>, walletList: List<String>? = null): Result {
        return try {
            // 1. Дістаємо повний список транзакцій між двома датами
            val fullTransactionList = firebaseRepository.getTransactionsBetweenDates(dates.first, dates.second, walletList)
            // 2. Розшифровуємо баланси
            val decryptedTransactions = fullTransactionList.map { transaction ->
                val decryptedAmount = cryptoRepository.decryptData(transaction.amountEnc, transaction.amountIv)
                val amountDouble = decryptedAmount.toDoubleOrNull() ?: throw Exception("Не вдалось розшифрувати транзакцію")
                transaction.copy(amount = amountDouble)
            }
            // 3. Сортуємо витрати і доходи
            val expensesTransactions = decryptedTransactions.filter { t ->
                when (t.type) {
                    TransactionType.Expense -> true
                    TransactionType.Transfer -> t.walletIdTo.isEmpty() && t.amount < 0
                    else -> false
                }
            }
            val incomeTransactions = decryptedTransactions.filter { t->
                when(t.type){
                    TransactionType.Income -> true
                    TransactionType.Transfer -> t.walletIdTo.isEmpty() && t.amount >0
                    else -> false
                }
            }
            val totalIncome = incomeTransactions.sumOf { it.amount }
            val totalExpenses = expensesTransactions.sumOf { it.amount }
            // 4. Ініціалізуємо мапу категорій у потрібному порядку
            val categorizedMap = (categoryList + "other").associateWith { mutableListOf<Transaction>() }

            // 5. Розподіляємо транзакції по категоріях
            expensesTransactions.forEach { transaction ->
                val category = transaction.categoryId
                if (categorizedMap.containsKey(category)) {
                    categorizedMap[category]?.add(transaction)
                } else {
                    categorizedMap["other"]?.add(transaction)
                }
            }

            // 6. Перетворюємо мапу з MutableList на List
            val result = categorizedMap.mapValues { it.value.toList() }

            Result.Success(result, totalExpenses, totalIncome)
        }catch (e:Exception){
            Log.d("SelectAdditionalPlanUseCase", "${e.message}")
            Result.Failure("Не вдалось розрахувати статистику. Спробуйте ще раз")
        }
    }
}