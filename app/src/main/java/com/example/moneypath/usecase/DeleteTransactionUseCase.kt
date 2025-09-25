package com.example.moneypath.usecase

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class DeleteTransactionUseCase@Inject constructor(
    private val repository: FirebaseRepository
) {

    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(transaction: Transaction): Result {
        return try {
            // Отримуємо баланс
            val balance = repository.getWalletBalance(transaction.walletId)
                ?: return Result.Failure("Гаманець не знайдено")


            // Логіка для типів транзакцій
            when (transaction.type) {
                TransactionType.Income -> {
                    repository.updateWalletBalance(transaction.walletId, balance - transaction.amount)
                }
                TransactionType.Expense -> {
                    repository.updateWalletBalance(transaction.walletId, balance - transaction.amount)
                }
                TransactionType.Transfer -> {
                    // Переказ зовнішній
                    when (transaction.description) {
                        "Зовнішній(дохід)" -> {
                            repository.updateWalletBalance(transaction.walletId, balance - transaction.amount)
                        }
                        "Зовнішній(витрата)" -> {
                            repository.updateWalletBalance(transaction.walletId, balance + transaction.amount)
                        }
                        else -> { // Внутрішній
                            val balanceTo = repository.getWalletBalance(transaction.walletIdTo)
                                ?: return Result.Failure("Гаманець не знайдено")
                                repository.updateWalletBalance(
                                    transaction.walletId,
                                    balance + transaction.amount
                                )
                                repository.updateWalletBalance(
                                    transaction.walletIdTo,
                                    balanceTo - transaction.amount
                                )
                        }
                    }
                }
            }

            // Додаємо саму транзакцію
            repository.deleteTransaction(transactionId = transaction.id)
            Result.Success

        } catch (e: Exception) {
            Log.d("AddTransactionUseCase", "Не вдалось видалити транзакцію ${e.message}")
            Result.Failure("Не вдалось видалити транзакцію. Спробуйте ще раз")
        }
    }
}