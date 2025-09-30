package com.example.moneypath.usecase.initialize

import android.util.Base64
import android.util.Log
import com.example.moneypath.data.local.EncryptedPrefsHelper
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class RetrieveAndDecryptDekUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository,
    private val prefs: EncryptedPrefsHelper
) {
    suspend operator fun invoke(): Boolean {
        // 1. Беремо пароль із локальних зашифрованих prefs
        val password = prefs.getPassword() ?: return false

        // 2. Дістаємо salt з Firebase і конвертуємо у ByteArray
        val saltBase64 = firebaseRepository.getSalt() ?: return false
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)

        // 3. Відновлюємо MasterKey
        val masterKey = cryptoRepository.deriveMasterKey(password, salt)

        // 4. Дістаємо wrappedDEK та IV з Firebase і конвертуємо у ByteArray
        val (wrappedDEK, iv) = firebaseRepository.getWrappedDEK() ?: return false

        // 5. Розгортаємо DEK
        val dek = cryptoRepository.unwrapDEK(wrappedDEK, iv, masterKey)
        // 6. Зберігаємо DEK у session
        cryptoRepository.saveSessionDEK(dek.encoded)

        val canaryPair = firebaseRepository.getCanaryData() ?:return false

        // Перевіряємо правильність пін-коду
        val decryptedCanary = cryptoRepository.decryptData(canaryPair.first, canaryPair.second)
        if (decryptedCanary != "test") {
            cryptoRepository.clearSessionDEK()
            Log.d("RetrieveUseCase", "Pin is incorrect")
            return false
        }else {
            Log.d("RetrieveUseCase", "Pin is correct")
            return true
        }
    }
}