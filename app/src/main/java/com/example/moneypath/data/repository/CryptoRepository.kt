package com.example.moneypath.data.repository

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class CryptoRepository @Inject constructor(){

    // Поточний Data Encryption Key (сесійний)
    private var sessionDEK: SecretKey? = null

    // --- Методи для sessionDEK ---
    fun saveSessionDEK(dek: ByteArray) {
        sessionDEK = SecretKeySpec(dek, "AES")
    }

    fun getSessionDEK(): ByteArray? = sessionDEK?.encoded

    fun clearSessionDEK() {
        sessionDEK = null
    }

    // --- Генерація ---
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

//    fun generateDEK(): SecretKey {
//        val keyGen = KeyGenerator.getInstance("AES")
//        keyGen.init(256)
//        return keyGen.generateKey()
//    }

    fun deriveMasterKey(password: String, salt: ByteArray): SecretKey {
        // Вибір алгоритму залежно від версії Android
        val algorithm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            "PBKDF2WithHmacSHA256"
        } else {
            "PBKDF2WithHmacSHA1"
        }
        // Специфікація ключа: пароль, сіль, кількість ітерацій, довжина ключа
        val spec = PBEKeySpec(password.toCharArray(), salt, 10_000, 256)
        val factory = SecretKeyFactory.getInstance(algorithm)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    // --- Обгортання та розгортання DEK ---
    fun wrapDEK(dek: SecretKey, masterKey: SecretKey): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.WRAP_MODE, masterKey, GCMParameterSpec(128, iv))
        val wrapped = cipher.wrap(dek)
        return Base64.encodeToString(wrapped, Base64.DEFAULT) to Base64.encodeToString(iv, Base64.DEFAULT)
    }

    fun unwrapDEK(wrappedDEK: String, iv: String, masterKey: SecretKey): SecretKey {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val wrappedBytes = Base64.decode(wrappedDEK, Base64.DEFAULT)
        val ivBytes = Base64.decode(iv, Base64.DEFAULT)
        cipher.init(Cipher.UNWRAP_MODE, masterKey, GCMParameterSpec(128, ivBytes))
        return cipher.unwrap(wrappedBytes, "AES", Cipher.SECRET_KEY) as SecretKey
    }

    // --- Шифрування/дешифрування даних з sessionDEK ---
    fun generateDEK(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    fun encryptData(data: String): Pair<String, String> {
        val dek = sessionDEK ?: throw IllegalStateException("DEK not initialized")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, dek, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT) to Base64.encodeToString(iv, Base64.DEFAULT)
    }

    fun decryptData(data: String, iv: String): String {
        val dek = sessionDEK ?: throw IllegalStateException("DEK not initialized")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val dataBytes = Base64.decode(data, Base64.DEFAULT)
        val ivBytes = Base64.decode(iv, Base64.DEFAULT)
        cipher.init(Cipher.DECRYPT_MODE, dek, GCMParameterSpec(128, ivBytes))
        val decrypted = cipher.doFinal(dataBytes)
        return String(decrypted)
    }
}