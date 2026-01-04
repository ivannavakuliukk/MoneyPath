package com.example.moneypath.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.domain.models.TransactionType
import com.example.moneypath.domain.usecase.business.DeleteTransactionUseCase
import com.example.moneypath.domain.usecase.crypto.GetTransactionByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionInfoViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase
): ViewModel(){

    data class UiState(
        val transaction: Transaction = Transaction(
            id = "",
            date = 0L,
            categoryId = "",
            amount = 0.0,
            description = "",
            type = TransactionType.Income,
            walletId = "",
            walletIdTo = "",
            amountEnc = "",
            amountIv = "",
        ),
        val walletName: String = "",
        val walletToName: String = "",

        val isLoadingTransaction: Boolean = false,
        val successTransaction: Boolean = true,

        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null

    )

    var uiState by mutableStateOf(UiState())

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    fun loadTransaction(transactionId: String){
        viewModelScope.launch {
            try {
                uiState = uiState.copy(
                    isLoadingTransaction = true
                )
                val transaction = getTransactionByIdUseCase(transactionId)?:
                throw IllegalStateException("Помилка при завантаженні транзакції")
                uiState = uiState.copy(transaction = transaction)

                val walletName = repository.getWalletById(walletId = uiState.transaction.walletId)?.name?: ""
                val walletNameTo = repository.getWalletById(walletId = uiState.transaction.walletIdTo)?.name?: ""
                Log.d("TransactionInfoViewModel", "$walletName, $walletNameTo")

                uiState = uiState.copy(
                    walletName = walletName,
                    walletToName = walletNameTo,
                    isLoadingTransaction = false,
                )
            }catch (e:Exception){
                uiState = uiState.copy(
                    isLoadingTransaction = false,
                    successTransaction = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteTransaction(){
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )
            val result = deleteTransactionUseCase.execute(uiState.transaction)
            uiState = when(result){
                is DeleteTransactionUseCase.Result.Success->
                    uiState.copy(
                        isLoading = false,
                        success = true
                    )

                is DeleteTransactionUseCase.Result.Failure->
                    uiState.copy(
                        isLoading = false,
                        error = result.message,
                        success = false
                    )
            }
        }
    }
}