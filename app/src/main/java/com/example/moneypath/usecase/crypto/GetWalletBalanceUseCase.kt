package com.example.moneypath.usecase.crypto

import android.util.Log
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class GetWalletBalanceUseCase @Inject constructor (
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(walletId: String): Double?{
        return try {
            val decrypted =  firebaseRepository.getWalletBalance(walletId)
            decrypted?.let { cryptoRepository.decryptData(it.first, it.second).toDoubleOrNull() }
        }catch (e:Exception){
            Log.d("GetWalletBalanceUseCase", "Error decrypting balance", e)
            null
        }
    }
}