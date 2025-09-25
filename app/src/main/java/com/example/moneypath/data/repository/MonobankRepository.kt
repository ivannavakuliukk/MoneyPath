package com.example.moneypath.data.repository

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import coil.network.HttpException
import com.example.moneypath.data.datasource.MonobankService
import com.example.moneypath.data.models.ClientInfo
import com.example.moneypath.data.models.MonoTransaction
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.utils.dateToUnix
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Репозиторій для роботи з Monobank API.
 *
 * Відповідає за:
 *  - збереження та отримання токена користувача (у зашифрованому вигляді через EncryptedSharedPreferences),
 *  - перевірку валідності токена,
 *  - виконання мережевих запитів до Monobank API.
 *
 * @property context використовується для створення зашифрованого сховища токена.
 * @property service екземпляр MonobankService для взаємодії з Monobank API.
 */
class MonobankRepository @Inject constructor(@ApplicationContext private val context: Context, private val service: MonobankService){

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "monobank_prefs", // ім’я файлу
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("monobank_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("monobank_token", null)
    }

    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    suspend fun isTokenValid(): Boolean{
        val token = getToken()?: return false
        return try{
            service.getClientInfo(token)
            true
        }catch (e:Exception){
            false
        }
    }

    fun clearToken() {
        sharedPreferences.edit().remove("monobank_token").apply()
    }

    suspend fun getTransactionBetween(fromDate: Long, toDate: Long):List<MonoTransaction>{
        return try{
            val token = getToken()?: return emptyList()
            service.getTransactions(token, fromDate, toDate)
        }catch (e: HttpException){
            Log.d("Monobank Repository", "HTTP error ${e.message}")
            emptyList()
        }catch(e: Exception){
            Log.d("Monobank Repository", "Other error ${e.message}")
            emptyList()
        }
    }

    suspend fun getTransactionByDate(token:String, date:String):List<MonoTransaction>{
        val from = dateToUnix(date)
        val to = from + 24*60*60  // додаємо 1 день
        return service.getTransactions(token, from, to)
    }
}