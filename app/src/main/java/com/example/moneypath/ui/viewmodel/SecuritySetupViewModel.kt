package com.example.moneypath.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.local.EncryptedPrefsHelper
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.initialize.GenerateAndStoreKeysUseCase
import com.example.moneypath.usecase.initialize.RetrieveAndDecryptDekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class SecuritySetupViewModel @Inject constructor(
    private val encryptedPrefsHelper: EncryptedPrefsHelper,
    private val firebaseRepository: FirebaseRepository,
    private val initializeUseCase: GenerateAndStoreKeysUseCase,
    private val retrieveUseCase: RetrieveAndDecryptDekUseCase
): ViewModel(){

    data class UiState(
        val password: String = "",
        val error: String? = null,
        val successPin: Boolean = false,

        val hasPin: Boolean = false,
        val hasSalt: Boolean = false,
        val keyStatus: KeyStatus = KeyStatus.UNKNOWN,
        val isLoadingStatus: Boolean = true,
        val isLoadingPin: Boolean = false,
        val successStatus : Boolean = false,
        val successKey: Boolean = false
    )

    enum class KeyStatus {
        FIRST_LAUNCH, // нічого немає: треба створити PIN
        READY,        // є і PIN, і salt → можна авторизуватись
        MISSING_SALT, // є PIN, але нема salt → проблема з базою
        MISSING_PIN,  // є salt, але нема PIN → локально не створений
        UNKNOWN
    }

    var uiState by mutableStateOf(UiState())

    init {
        loadKeyMaterial()
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    fun onPasswordChange(newValue: String){
        uiState = uiState.copy(password = newValue)
    }

    fun savePassword(){
        if(uiState.password.isEmpty() || uiState.password.toIntOrNull() == null){
            uiState = uiState.copy(
                error = "Введіть коректний пароль з 5 цифр",
                successPin = false,
                password = ""
            )
        } else {
            encryptedPrefsHelper.savePassword(uiState.password)
            uiState = uiState.copy(
                password = "",
                successPin = true
            )
        }
    }

    fun loadKeyMaterial() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoadingStatus = true)
//                encryptedPrefsHelper.removePassword()
                val hasPin = !encryptedPrefsHelper.getPassword().isNullOrEmpty()
                val hasSalt = !firebaseRepository.getSalt().isNullOrEmpty()
                val status = when {
                    !hasPin && !hasSalt -> KeyStatus.FIRST_LAUNCH
                    hasPin && hasSalt -> KeyStatus.READY
                    !hasPin && hasSalt -> KeyStatus.MISSING_PIN
                    hasPin && !hasSalt -> KeyStatus.MISSING_SALT
                    else -> KeyStatus.UNKNOWN
                }
                uiState = uiState.copy(
                    hasPin = hasPin,
                    hasSalt = hasSalt,
                    keyStatus = status,
                    isLoadingStatus = false,
                    successStatus = true
                )
            }catch (e:Exception){
                uiState = uiState.copy(
                    error = "Не вдалось авторизуватись",
                    isLoadingStatus = false,
                    successStatus = false
                )
                Log.d("SecuritySetupViewModel", "Error load key material")
            }
        }
    }

    fun initializeKeys(){
        viewModelScope.launch {
            uiState = uiState.copy(keyStatus = KeyStatus.UNKNOWN)
            var success: Boolean
            withContext(Dispatchers.IO){
                try{
                    success = initializeUseCase.invoke()
                }catch(e:Exception){
                    Log.e("SecuritySetupViewModel", "Error initializing keys", e)
                    success = false
                }
            }
            if(success) uiState = uiState.copy(successKey = true)
            else{
                encryptedPrefsHelper.removePassword()
                loadKeyMaterial()
                uiState = uiState.copy(error = "Щось пішло не так, спробуйте ще раз")
            }
        }
    }

    fun retrieveKeys(){
        viewModelScope.launch {
            uiState = uiState.copy(keyStatus = KeyStatus.UNKNOWN)
            var success: Boolean
            withContext(Dispatchers.IO){
                try {
                    success = retrieveUseCase.invoke()
                } catch (e: Exception) {
                    Log.e("SecuritySetupViewModel", "Error retrieving keys", e)
                    success = false
                }
            }
            if(success) uiState = uiState.copy(successKey = true)
            else{
                encryptedPrefsHelper.removePassword()
                loadKeyMaterial()
                uiState = uiState.copy(error = "Щось пішло не так, спробуйте ще раз")
            }
        }
    }

}