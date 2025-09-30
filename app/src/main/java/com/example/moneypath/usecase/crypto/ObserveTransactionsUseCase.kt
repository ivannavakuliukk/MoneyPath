package com.example.moneypath.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(
        date: Long,
        onUpdate: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseRepository.getTransactionsByDate(
            date = date,
            onUpdate = { transactions ->
                val decrypted = transactions.mapNotNull { t ->
                    try {
                        val amount = cryptoRepository.decryptData(t.amountEnc, t.amountIv).toDouble()
                        t.copy(amount = amount)
                    } catch (e: Exception) {
                        Log.d("ObserveTransactionUseCase", "Error decrypting transactions", e)
                        null
                    }
                }
                onUpdate(decrypted)
            },
            onError = onError
        )
    }
}