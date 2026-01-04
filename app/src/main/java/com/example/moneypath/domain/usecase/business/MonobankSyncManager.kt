package com.example.moneypath.domain.usecase.business

import android.content.Context
import android.util.Log
import com.example.moneypath.domain.repository.MonobankRepository
import com.example.moneypath.domain.usecase.crypto.AddTransactionsUseCase
import com.example.moneypath.domain.usecase.crypto.UpdateBalanceUseCase
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
 * @property addTransactionsUseCase use case для роботи з Firebase
 * @property updateBalanceUseCase use case для роботи з Firebase
 */

class MonobankSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val monobankRepository: MonobankRepository,
    private val addTransactionsUseCase: AddTransactionsUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase
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


    suspend fun syncSinceLastVisit(): Result {
        val from = getLastSync()
        val to = System.currentTimeMillis()/1000

        if((to - from) > 65 ) {
            val monoTransactions =
                monobankRepository.getTransactionsBetween(from, to).sortedBy { it.date }
            setLastSync(to)

            if (monoTransactions.isNotEmpty()) {
                val addSuccess =
                    addTransactionsUseCase(transactions = monoTransactions)
                val updateSuccess = updateMonoWallet()
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

    private suspend fun updateMonoWallet(): Boolean{
        return updateBalanceUseCase("mono",
            monobankRepository.getAccountInfo()?.balance?.toDouble()?.div(100) ?: 0.0)
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