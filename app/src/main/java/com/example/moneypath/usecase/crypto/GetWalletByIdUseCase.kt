package com.example.moneypath.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class GetWalletByIdUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke(walletId: String): Wallet? {
        return try {
            val wallet = firebaseRepository.getWalletById(walletId) ?: return null
            val decryptedBalance = cryptoRepository.decryptData(wallet.balanceEnc, wallet.balanceIv)
            val balanceDouble = decryptedBalance.toDoubleOrNull() ?: return null
            wallet.copy(balance = balanceDouble)
        } catch (e: Exception) {
            Log.d("GetWalletByIdUseCase", "Error decrypting balance", e)
            null
    }
    }
}
