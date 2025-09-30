package com.example.moneypath.usecase.business

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.crypto.GetWalletBalanceUseCase
import com.example.moneypath.usecase.crypto.UpdateBalanceUseCase
import javax.inject.Inject

// Бізнес-логіка оновлення балансу при додаванні транзакції
class AddTransactionUseCase@Inject constructor(
    private val getWalletBalanceUseCase: GetWalletBalanceUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
    private val firebaseRepository: FirebaseRepository,
    private val cryptoRepository: CryptoRepository
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
                    updateBalanceUseCase(transaction.walletId, balance + transaction.amount)
                }
                TransactionType.Expense -> {
                    if (balance < -transaction.amount)
                        return Result.Failure("Недостатньо коштів")
                    updateBalanceUseCase(transaction.walletId, balance + transaction.amount)
                }
                TransactionType.Transfer -> {
                    if(transaction.description == "Зовнішній(витрата)" || transaction.description == "Внутрішній") {
                        if (balance < transaction.amount)
                            return Result.Failure("Недостатньо коштів для переказу")
                    }

                    // Переказ зовнішній
                    when (transaction.description) {
                        "Зовнішній(дохід)" -> {
                            updateBalanceUseCase(transaction.walletId, balance + transaction.amount)
                        }
                        "Зовнішній(витрата)" -> {
                            updateBalanceUseCase(transaction.walletId, balance - transaction.amount)
                        }
                        else -> { // Внутрішній
                            val balanceTo = getWalletBalanceUseCase(transaction.walletIdTo)
                                ?: return Result.Failure("Гаманець не знайдено")
                            if (transaction.walletIdTo == transaction.walletId) {
                                return Result.Failure("Ви обрали один і той самий гаманець")
                            }else {
                                   updateBalanceUseCase(
                                        transaction.walletId,
                                        balance - transaction.amount
                                    )
                                    updateBalanceUseCase(
                                        transaction.walletIdTo,
                                        balanceTo + transaction.amount
                                    )
                            }
                        }
                    }
                }
            }

            // Додаємо саму транзакцію (зашифровану)
            val encryptedBalance = cryptoRepository.encryptData(transaction.amount.toString())
            firebaseRepository.addTransaction(transaction.copy(
                amount = 0.0,
                amountEnc = encryptedBalance.first,
                amountIv = encryptedBalance.second
            ))
            Result.Success

        } catch (e: Exception) {
            Log.d("AddTransactionUseCase", "Не вдалось додати транзакцію ${e.message}")
            Result.Failure("Не вдалось додати транзакцію. Спробуйте ще раз")
        }
    }
}