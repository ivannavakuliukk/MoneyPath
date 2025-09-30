package com.example.moneypath.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.models.WalletSource
import com.example.moneypath.data.models.WalletType
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.data.repository.MonobankRepository
import com.example.moneypath.usecase.business.MonobankSyncManager
import com.example.moneypath.usecase.crypto.AddOrUpdateWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherViewModel @Inject constructor(
    private val repository: MonobankRepository,
    private val firebaseRepository: FirebaseRepository,
    private val syncManager: MonobankSyncManager,
    private val addOrUpdateWalletUseCase: AddOrUpdateWalletUseCase
):ViewModel() {

    data class UiState(
        val token: String = "",
        val hasToken:Boolean = false,
        val tokenValid: Boolean? = null,
        val error: String? = null,

        val shouldNavigateToSignIn: Boolean = false
    )

    var uiState by mutableStateOf(UiState())

    init {
        loadToken()
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }
    fun onTokenChange(newValue: String){
        uiState = uiState.copy(token = newValue)
    }

    private fun loadToken(){
        val hasToken = repository.hasToken()
        uiState = uiState.copy(
            hasToken = hasToken
        )
    }

    fun saveToken(){
        viewModelScope.launch {
            if (uiState.token.isBlank()) {
                uiState = uiState.copy(
                    tokenValid = false,
                    error = "Токен не може бути порожнім"
                )
                return@launch
            }

            try {
                repository.saveToken(uiState.token)
                loadToken()

                val valid = repository.isTokenValid()

                if (!valid) {
                    clearToken()
                    uiState = uiState.copy(token = "", hasToken = false)
                } else {
                    addOrUpdateWalletUseCase(
                        Wallet(
                            id = "mono",
                            type = WalletType.Card,
                            source = WalletSource.Api,
                            name = "mono"
                        )
                    )

                    val result = syncManager.syncSinceLastVisit()
                    if (result is MonobankSyncManager.Result.Failure) {
                        uiState = uiState.copy(error = result.message)
                    }
                }

                uiState = uiState.copy(
                    token = "", // Очищення бо він більше не потрібен
                    tokenValid = valid,
                    error = if (!valid) "Ви ввели некоректний токен" else uiState.error
                )

            } catch (e: Exception) {
                Log.e("OtherViewModel", "Error saving token", e)
                uiState = uiState.copy(
                    tokenValid = false,
                    error = "Сталася помилка при збереженні токена"
                )
            }
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            repository.clearToken()
            firebaseRepository.deleteWallet("mono")
            syncManager.clearPrefs()
            loadToken()
        }
    }

    fun logOut(){
        viewModelScope.launch {
            uiState = if(firebaseRepository.logOut()){
                uiState.copy(
                    shouldNavigateToSignIn = true
                )
            }else{
                uiState.copy(error = "Не вдалось вийти з акаунту")
            }
        }
    }

    fun deleteAccount(){
        viewModelScope.launch {
            uiState = if(firebaseRepository.deleteAccount()){
                uiState.copy(
                    shouldNavigateToSignIn = true
                )
            }else{
                uiState.copy(error = "Не вдалось видалити акаунт")
            }
        }
    }
}