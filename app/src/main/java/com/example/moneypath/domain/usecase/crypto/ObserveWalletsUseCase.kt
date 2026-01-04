package com.example.moneypath.domain.usecase.crypto

import android.util.Log
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class ObserveWalletsUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository
) {
    operator fun invoke(
        onUpdate: (List<Wallet>) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseRepository.getWallets(
            onUpdate = { wallets ->
                val decrypted = wallets.mapNotNull { w ->
                    try {
                        val balance = cryptoRepository.decryptData(w.balanceEnc, w.balanceIv).toDouble()
                        w.copy(balance = balance)
                    } catch (e: Exception) {
                        Log.d("ObserveWalletsUseCase", "Error decrypting wallets", e)
                        null
                    }
                }
                onUpdate(decrypted)
            },
            onError = onError
        )
    }
}
