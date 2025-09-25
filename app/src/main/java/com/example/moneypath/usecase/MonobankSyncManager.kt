package com.example.moneypath.usecase

import android.content.Context
import android.util.Log
import com.example.moneypath.data.models.MonoTransaction
import com.example.moneypath.data.models.toTransaction
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.data.repository.MonobankRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Domain class - менеджер синхронізації який працює з двома репозиторіями,
 * відповідає за синхронізацію транзакцій
 *
 * Дістає транзакції з MonobankRepository, перетворює в внутрішню модель додатку
 * викликає FirebaseRepository для запису.
 * Запам'ятовує час останньої синхронізації та підтримує пулінг - синхронізацію щодві хвилини
 *
 * @property context для доступу до локальних налаштувань (SharedPreferences)
 * @property monobankRepository екземпляр MonobankRepository
 * @property firebaseRepository екземпляр FirebaseRepository
 */

class MonobankSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val monobankRepository: MonobankRepository,
    private val firebaseRepository: FirebaseRepository
){
    private var pollingJob: Job? = null
    private val prefs = context.getSharedPreferences("monobank_sync", Context.MODE_PRIVATE)

    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    private fun getLastSync():Long = prefs.getLong("last_sync", System.currentTimeMillis()/1000 - 3*24*60*60 )
    private fun setLastSync(timestamp:Long){
        prefs.edit().putLong("last_sync", timestamp).apply()
    }


    suspend fun syncSinceLastVisit(): Result{
        val from = getLastSync()
        val to = System.currentTimeMillis()/1000

        if((to - from) > 65 ) {
            val monoTransactions =
                monobankRepository.getTransactionBetween(from, to).sortedBy { it.time }
            setLastSync(to)

            if (monoTransactions.isNotEmpty()) {
                val addSuccess =
                    firebaseRepository.addTransactions(monoTransactions.map { it.toTransaction() })
                val updateSuccess = updateMonoWallet(monoTransactions)
                if (addSuccess && updateSuccess) {
                    return Result.Success
                } else {
                    Log.d("MonobankSyncManager", "Failed to sync transactions")
                    return Result.Failure("Помилка при завантажені даних. Видаліть токен та додайте ще раз.")
                }
            } else {
                Log.d("MonobankSyncManager", "No transactions to sync")
                return Result.Success
            }
        }else{
            Log.d("MonobankSyncManager", "To short duration from last sync")
            return Result.Success
        }
    }

    private suspend fun updateMonoWallet(transactions:List<MonoTransaction>): Boolean{
        return firebaseRepository.updateWalletBalance("mono", transactions.last().balance.toDouble()/100)
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(2 * 60 * 1000)
                syncSinceLastVisit()
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
    }

    fun clearPrefs() {
        prefs.edit().clear().apply()
    }

}