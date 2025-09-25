package com.example.moneypath.usecase

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.repository.FirebaseRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

// Бізнес-логіка оновлення балансу при додаванні транзакції
class AddTransactionUseCase@Inject constructor(
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
                    repository.updateWalletBalance(transaction.walletId, balance + transaction.amount)
                }
                TransactionType.Expense -> {
                    if (balance < -transaction.amount)
                        return Result.Failure("Недостатньо коштів")
                    repository.updateWalletBalance(transaction.walletId, balance + transaction.amount)
                }
                TransactionType.Transfer -> {
                    if(transaction.description == "Зовнішній(витрата)" || transaction.description == "Внутрішній") {
                        if (balance < transaction.amount)
                            return Result.Failure("Недостатньо коштів для переказу")
                    }

                    // Переказ зовнішній
                    when (transaction.description) {
                        "Зовнішній(дохід)" -> {
                            repository.updateWalletBalance(transaction.walletId, balance + transaction.amount)
                        }
                        "Зовнішній(витрата)" -> {
                            repository.updateWalletBalance(transaction.walletId, balance - transaction.amount)
                        }
                        else -> { // Внутрішній
                            val balanceTo = repository.getWalletBalance(transaction.walletIdTo)
                                ?: return Result.Failure("Гаманець не знайдено")
                            if (transaction.walletIdTo == transaction.walletId) {
                                return Result.Failure("Ви обрали один і той самий гаманець")
                            }else {
                                    repository.updateWalletBalance(
                                        transaction.walletId,
                                        balance - transaction.amount
                                    )
                                    repository.updateWalletBalance(
                                        transaction.walletIdTo,
                                        balanceTo + transaction.amount
                                    )
                            }
                        }
                    }
                }
            }

            // Додаємо саму транзакцію
            repository.addTransaction(transaction)
            Result.Success

        } catch (e: Exception) {
            Log.d("AddTransactionUseCase", "Не вдалось додати транзакцію ${e.message}")
            Result.Failure("Не вдалось додати транзакцію. Спробуйте ще раз")
        }
    }
}