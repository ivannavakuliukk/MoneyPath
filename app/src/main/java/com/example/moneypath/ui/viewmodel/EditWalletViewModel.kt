package com.example.moneypath.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.domain.usecase.crypto.AddOrUpdateWalletUseCase
import com.example.moneypath.domain.usecase.crypto.GetWalletByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditWalletViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    addOrUpdateWalletUseCase: AddOrUpdateWalletUseCase,
    private val getWalletByIdUseCase: GetWalletByIdUseCase
): AddWalletViewModel(addOrUpdateWalletUseCase){

    fun loadWalletData(walletId:String){
        viewModelScope.launch {
            try{
                val wallet = getWalletByIdUseCase(walletId)?:
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