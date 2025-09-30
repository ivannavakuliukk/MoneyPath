package com.example.moneypath.usecase.initialize

import android.util.Base64
import com.example.moneypath.data.local.EncryptedPrefsHelper
import com.example.moneypath.data.repository.CryptoRepository
import com.example.moneypath.data.repository.FirebaseRepository
import javax.inject.Inject

class GenerateAndStoreKeysUseCase @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val firebaseRepository: FirebaseRepository,
    private val prefs: EncryptedPrefsHelper
) {
    suspend operator fun invoke(): Boolean {
        // 1. Беремо пароль із prefs
        val password = prefs.getPassword() ?: return false

        // 2. Генеруємо salt
        val salt = cryptoRepository.generateSalt()
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        firebaseRepository.addSalt(saltBase64)

        // 3. Генеруємо DEK
        val dek = cryptoRepository.generateDEK()

        // 4. Створюємо MasterKey
        val masterKey = cryptoRepository.deriveMasterKey(password, salt)

        // 5. Обгортаємо DEK
        val (wrappedDEK, iv) = cryptoRepository.wrapDEK(dek, masterKey)
        // 6. Зберігаємо wrappedDEK і iv у Firebase
        firebaseRepository.addWrappedDEK(wrappedDEK, iv)

        // 7. Зберігаємо DEK у session
        cryptoRepository.saveSessionDEK(dek.encoded)

        // 8. Додаємо canary (тестовий рядок)
        val (canaryEncrypted, canaryIv) = cryptoRepository.encryptData("test")
        firebaseRepository.addCanaryData(canaryEncrypted, canaryIv)

        return true
    }
}