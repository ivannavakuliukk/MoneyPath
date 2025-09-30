package com.example.moneypath.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.models.WalletSource
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.usecase.crypto.AddOrUpdateWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


// ViewModel сторінки AddWalletScreen
@HiltViewModel
open class AddWalletViewModel @Inject constructor(
    private val addOrUpdateWalletUseCase: AddOrUpdateWalletUseCase
): ViewModel() {

    data class UiState(
        val name: String = "",
        val type: WalletType = WalletType.Cash,
        val balance: Double = 0.0,

        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null,
    )

    var uiState by mutableStateOf(UiState())

    fun onNameChange(newValue: String){
        uiState = uiState.copy(name = newValue)
    }

    fun onTypeChange(newValue: WalletType){
        uiState = uiState.copy(type = newValue)
    }

    fun onBalanceChange(newValue: Double){
        uiState = uiState.copy(balance = newValue)
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    fun addOrUpdateWallet(walletId:String = ""){
        viewModelScope.launch {
            if(uiState.name.isEmpty()|| uiState.name.isBlank() || uiState.name.length>40 || uiState.name.length<3){
                uiState = uiState.copy(error = "Введіть коректне ім'я: хоча б 3 знаки")
                return@launch
            }
            val result = addOrUpdateWalletUseCase(
                Wallet(walletId,
                uiState.name,
                uiState.type, uiState.balance, WalletSource.Manual))
            uiState = if(result){
                uiState.copy(
                    isLoading = false,
                    success = true,
                    error = null)
            }else{
                uiState.copy(isLoading = false,
                    success = false,
                    error = "Не вдалось додати/редагувати гаманець. Спробуйте ще раз")
            }
        }
    }
}