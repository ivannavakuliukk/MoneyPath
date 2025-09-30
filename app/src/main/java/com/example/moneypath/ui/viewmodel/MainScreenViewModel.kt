package com.example.moneypath.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.crypto.ObserveTransactionsUseCase
import com.example.moneypath.usecase.crypto.ObserveWalletsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// ViewModel сторінки MainScreen
@HiltViewModel
open class MainScreenViewModel @Inject constructor (
    private val repository: FirebaseRepository,
    private val observeWalletsUseCase: ObserveWalletsUseCase,
    private val observeTransactionsUseCase: ObserveTransactionsUseCase
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
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())

    init{
        loadUser()
        loadWallets()
        loadTransactionsByDate(System.currentTimeMillis()/1000)
    }

    fun onDateChange(newDate:Long){
        uiState = uiState.copy(date = newDate, transactions = emptyList())
        loadTransactionsByDate(newDate)
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
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
            onUpdate = {newTransactions->
               uiState= uiState.copy(
                   transactions = newTransactions,
                   isTransactionLoading = false,
                   isTransactionSuccess = true
               ) },
            onError = {newError->
               uiState= uiState.copy(
                   error = newError,
                   isTransactionLoading = false,
                   isTransactionSuccess = false
               )
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeWalletsEventListener()
        repository.removeTransactionsEventListener()
    }

}