package com.example.moneypath.domain.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class AddOrUpdateWalletUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(wallet: Wallet): Boolean {
        return try {
            // Шифруємо баланс
            val (enc, iv) = cryptoRepository.encryptData(wallet.balance.toString())

            val encryptedWallet = wallet.copy(
                balance = 0.0,
                balanceEnc = enc,
                balanceIv = iv
            )
            firebaseRepository.addOrUpdateWallet(encryptedWallet)
        }catch (e:Exception){
            Log.d("AddOrUpdateWalletUseCase", "Error decrypting balance", e)
            false
        }
    }
}
