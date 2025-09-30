package com.example.moneypath.usecase.business

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.crypto.GetWalletBalanceUseCase
import com.example.moneypath.usecase.crypto.UpdateBalanceUseCase
import javax.inject.Inject

class DeleteTransactionUseCase@Inject constructor(
    private val repository: FirebaseRepository,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
    private val getWalletBalanceUseCase: GetWalletBalanceUseCase
) {

    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(transaction: Transaction): Result {
        return try {
            // Отримуємо баланс
            val balance = getWalletBalanceUseCase(transaction.walletId)
                ?: return Result.Failure("Гаманець не знайдено")


            // Логіка для типів транзакцій
            when (transaction.type) {
                TransactionType.Income -> {
                    updateBalanceUseCase(transaction.walletId, balance - transaction.amount)
                }
                TransactionType.Expense -> {
                    updateBalanceUseCase(transaction.walletId, balance - transaction.amount)
                }
                TransactionType.Transfer -> {
                    // Переказ зовнішній
                    when (transaction.description) {
                        "Зовнішній(дохід)" -> {
                            updateBalanceUseCase(transaction.walletId, balance - transaction.amount)
                        }
                        "Зовнішній(витрата)" -> {
                            updateBalanceUseCase(transaction.walletId, balance + transaction.amount)
                        }
                        else -> { // Внутрішній
                            val balanceTo = getWalletBalanceUseCase(transaction.walletIdTo)
                                ?: return Result.Failure("Гаманець не знайдено")
                                updateBalanceUseCase(
                                    transaction.walletId,
                                    balance + transaction.amount
                                )
                                updateBalanceUseCase(
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