package com.example.moneypath.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class AddTransactionsUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
){
    suspend operator fun invoke(transactions: List<Transaction>):Boolean{
        return try{
            val encryptedTransactions = transactions.map { transaction->
                val (enc, iv) = cryptoRepository.encryptData(transaction.amount.toString())
                transaction.copy(
                    amount = 0.0,
                    amountEnc = enc,
                    amountIv = iv
                )

            }
            firebaseRepository.addTransactions(encryptedTransactions)
            true
        }catch (e:Exception){
            Log.d("AddTransactionsUseCase", "Error encrypting transactions amounts")
            false
        }
    }
}