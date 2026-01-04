package com.example.moneypath.domain.usecase.crypto

import android.util.Log
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class UpdateBalanceUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(walletId:String, newBalance: Double): Boolean{
        return try{
            val (enc, iv) = cryptoRepository.encryptData(newBalance.toString())
            firebaseRepository.updateWalletBalance(walletId, enc, iv)
            true
        }catch (e:Exception){
            Log.d("GetWalletByIdUseCase", "Error encrypting balance", e)
            false
        }
    }

}