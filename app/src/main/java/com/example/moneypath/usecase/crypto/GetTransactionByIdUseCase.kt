package com.example.moneypath.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(transactionId: String): Transaction? {
        return try {
            val transaction = firebaseRepository.getTransactionById(transactionId) ?: return null
            val decryptedAmount = cryptoRepository.decryptData(transaction.amountEnc, transaction.amountIv)
            val amountDouble = decryptedAmount.toDoubleOrNull() ?: return null
            transaction.copy(amount = amountDouble)
        } catch (e: Exception) {
            Log.d("GetTransactionByIdUseCase", "Error decrypting amount", e)
            null
        }
    }
}
