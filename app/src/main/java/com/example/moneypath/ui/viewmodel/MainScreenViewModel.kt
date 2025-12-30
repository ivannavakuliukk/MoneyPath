package com.example.moneypath.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneypath.data.local.PrefsHelper
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.business.DeletePlanUseCase
import com.example.moneypath.usecase.crypto.ObserveTransactionsGoalUseCase
import com.example.moneypath.usecase.crypto.ObserveTransactionsUseCase
import com.example.moneypath.usecase.crypto.ObserveWalletsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

// ViewModel сторінки MainScreen
@HiltViewModel
open class MainScreenViewModel @Inject constructor (
    private val repository: FirebaseRepository,
    private val observeWalletsUseCase: ObserveWalletsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val observeTransactionsGoalUseCase: ObserveTransactionsGoalUseCase,
    private val helper: PrefsHelper,
    private val deletePlanUseCase: DeletePlanUseCase
): ViewModel(){

    data class UiState(
        val userName: String = "Гість",
        val userPhotoUrl: Uri? = null,
        val wallets: List<Wallet> = emptyList(),
        val transactions: List<Transaction> = emptyList(),
        val date: Long = System.currentTimeMillis()/1000,

        val isWalletsLoading: Boolean = false,
        val isTransactionLoading: Boolean = false,
        val isWalletsSuccess: Boolean = false,
        val isTransactionSuccess: Boolean = false,
        val error: String? = null,

        val isGoal: Boolean = false,
        val goalTransactionsAmount: Double? = null,
        val goalName: String? = null,
        val goalAmount: Int? = null,
        val isContinued:Boolean = false,
        val planEnd: Long? = null
    )

    var uiState by mutableStateOf(UiState())

    init{
        loadUser()
        loadWallets()
        loadTransactionsByDate(System.currentTimeMillis()/1000)
//        helper.saveGoalAmount(40000)
//        helper.saveGoalName("Ноутбук")
        val goalName = helper.getGoalName()
        val goalAmount = helper.getGoalAmount()
        val isContinued = helper.getContinued()
        if(goalAmount!=null && goalName!=null){
            uiState = uiState.copy(
                goalName = goalName,
                goalAmount = goalAmount,
                isGoal = true,
                isContinued = isContinued
            )
            loadTransactionsGoal()
            loadPlanEnd()
        }

    }

    fun onDateChange(newDate:Long){
        uiState = uiState.copy(date = newDate, transactions = emptyList())
        loadTransactionsByDate(newDate)
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    fun setContinued(){
        uiState = uiState.copy(isContinued = true)
        helper.saveContinued(true)
    }

    private fun loadUser(){
        val user = repository.getCurrentUser()
        uiState = uiState.copy(
            userName = user?.displayName ?: "Гість",
            userPhotoUrl = user?.photoUrl
        )
    }

    fun onErrorChange(error: String?){
        uiState = uiState.copy(error = error)
    }

    private fun loadWallets(){
        uiState = uiState.copy(
            isWalletsLoading = true,
            isWalletsSuccess = false
        )
        observeWalletsUseCase(
            onUpdate = { newWallets ->
                uiState = uiState.copy(
                    wallets = newWallets,
                    isWalletsLoading = false,
                    isWalletsSuccess = true
                ) },
            onError = { newError ->
                uiState = uiState.copy(
                    error = newError,
                    isWalletsLoading = false,
                    isWalletsSuccess = false
                )
            }
        )
    }

    private fun loadTransactionsByDate(date:Long){
        uiState = uiState.copy(
            isTransactionLoading = true,
            isTransactionSuccess = false
        )
        observeTransactionsUseCase(
            date = date,
            onUpdate = { newTransactions ->
                uiState = uiState.copy(
                    transactions = newTransactions,
                    isTransactionLoading = false,
                    isTransactionSuccess = true
                )
            },
            onError = { newError ->
                uiState = uiState.copy(
                    error = newError,
                    isTransactionLoading = false,
                    isTransactionSuccess = false
                )
            }
        )
    }

    private fun loadTransactionsGoal(){
        observeTransactionsGoalUseCase(
            onUpdate = {newTransactions->
                uiState= uiState.copy(
                    goalTransactionsAmount = newTransactions.sumOf { it.amount },
                ) },
            onError = {newError->
                uiState= uiState.copy(
                    error = newError
                )
            }
        )
    }

    fun deletePlan(){
        viewModelScope.launch {
            deletePlanUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeWalletsEventListener()
        repository.removeTransactionsEventListener()
        repository.removeTransactionsGoalEventListener()
    }

    fun loadPlanEnd(){
        val result :Long?
        if(uiState.isGoal){
            result = helper.getPlanEnd()
            uiState = uiState.copy(planEnd = result)
            if(result == null){
                viewModelScope.launch {
                    val date = repository.getTotalPlanEnd()
                    helper.savePlanEnd(date?:0L)
                    uiState = uiState.copy(planEnd = date)
                }
            }
        }
        Log.d("MainScreenViewMode", uiState.planEnd.toString())

    }


}