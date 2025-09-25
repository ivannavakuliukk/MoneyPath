package com.example.moneypath.ui.viewmodel

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.models.WalletSource
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditWalletViewModel @Inject constructor(private val repository: FirebaseRepository): AddWalletViewModel(repository){

    fun loadWalletData(walletId:String){
        viewModelScope.launch {
            try{
                val wallet = repository.getWalletById(walletId)?:
                throw IllegalStateException("Помилка при завантаженні даних гаманця")
                uiState = uiState.copy(
                    name = wallet.name,
                    balance = wallet.balance,
                    type = wallet.type
                )
            }catch (e: Exception){
                uiState = uiState.copy(
                error = e.message)
            }
        }
    }


    fun deleteWallet(walletId: String){
        uiState = uiState.copy(
            isLoading = true,
            error = null,
            success = false
        )
        viewModelScope.launch {
            val result = repository.deleteWallet(walletId)

            uiState = if(result) {
                uiState.copy(
                    isLoading = false,
                    error = null,
                    success = true
                )
            }else{
                uiState.copy(
                    isLoading = false,
                    error = "Помилка при видаленні гаманця. Спробуйте ще раз.",
                    success = false
                )
            }
        }
    }

}