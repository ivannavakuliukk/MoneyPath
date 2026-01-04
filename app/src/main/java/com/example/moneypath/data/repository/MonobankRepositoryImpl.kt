package com.example.moneypath.data.repository

import android.util.Log
import coil.network.HttpException
import com.example.moneypath.data.local.MonobankLocalDataSource
import com.example.moneypath.data.remote.MonobankRemoteDataSource
import com.example.moneypath.domain.models.Account
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.domain.repository.MonobankRepository
import javax.inject.Inject

/**
 * Репозиторій для роботи з Monobank API.
 *
 * Відповідає за:
 *  - збереження та отримання токена користувача (у зашифрованому вигляді через EncryptedSharedPreferences),
 *  - перевірку валідності токена,
 *  - виконання мережевих запитів до Monobank API.
 *
 */
class MonobankRepositoryImpl @Inject constructor(
    private val remote: MonobankRemoteDataSource,
    private val local: MonobankLocalDataSource
): MonobankRepository{

    override fun saveToken(token: String) {
        local.saveToken(token)
    }

    override fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    fun getToken(): String?{
        return local.getToken()
    }

    override suspend fun isTokenValid(): Boolean{
        return true
        // закоментувала бо забагато запитів було
//        val token = getToken()?: return false
//        return try{
//            remote.getClientInfo(token)
//            true
//        }catch (e:Exception){
//            Log.d("MonobankRepository", "${e.message}")
//            false
//        }
    }

    override fun clearToken() {
        local.clearToken()
    }

    override suspend fun getTransactionsBetween(from: Long, to: Long):List<Transaction>{
        return try{
            val token = getToken()?: return emptyList()
            remote.getTransactions(token, from, to)
        }catch (e: HttpException){
            Log.d("Monobank Repository", "HTTP error ${e.message}")
            emptyList()
        }catch(e: Exception){
            Log.d("Monobank Repository", "Other error ${e.message}")
            emptyList()
        }
    }

    override suspend fun getAccountInfo(): Account?{
        return try{
            val token = getToken()?: return null
            remote.getClientInfo(token)
        }catch (e: HttpException){
            Log.d("Monobank Repository", "HTTP error ${e.message}")
            null
        }catch(e: Exception){
            Log.d("Monobank Repository", "Other error ${e.message}")
            null
        }
    }
}