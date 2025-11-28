package com.example.moneypath.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION")


class EncryptedPrefsHelper @Inject constructor(@ApplicationContext private val context: Context) {
    // Створюємо майстер-ключ для шифрування даних
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // Створюємо зашифровані SharedPreferences з вказаним майстер-ключем
    private val prefs = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Методи для збереження, отримання та видалення
    fun savePassword(password: String) = prefs.edit().putString("user_password", password).apply()
    fun getPassword(): String? = prefs.getString("user_password", null)
    fun removePassword() = prefs.edit().remove("user_password").apply()

    fun saveToken(token: String) = prefs.edit().putString("monobank_token", token).apply()
    fun getToken(): String? = prefs.getString("monobank_token", null)
    fun removeToken() = prefs.edit().remove("monobank_token").apply()

    fun saveGoalName(name: String) = prefs.edit().putString("goal_name", name).apply()
    fun getGoalName(): String? = prefs.getString("goal_name", null)
    fun removeGoalName() = prefs.edit().remove("goal_name").apply()

    fun saveGoalAmount(amount: Int) = prefs.edit().putString("goal_amount", amount.toString()).apply()
    fun getGoalAmount(): String? = prefs.getString("goal_amount", null)
    fun removeGoalAmount() = prefs.edit().remove("goal_amount").apply()

}
