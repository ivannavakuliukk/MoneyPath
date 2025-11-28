package com.example.moneypath.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.Category
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.TransactionType
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.business.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class AddTransactionViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val addTransactionUseCase: AddTransactionUseCase
): ViewModel(){

    data class UiState(
        val amount: String = "0.0",
        val date: Long = System.currentTimeMillis()/1000,
        val categoryId: String = "food",
        val description: String = "",
        val type: TransactionType = TransactionType.Expense,
        val walletId: String = "",
        // для переказу
        val walletIdTo: String = "",
        val isGoal: Boolean = false,

        val categories: List<Category> = Categories.expensesCategory,
        val wallets: List<Wallet> = emptyList(),

        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null,
    )

    var uiState by mutableStateOf(UiState())

    init {
        loadWallets()
    }

    fun onAmountChange(newValue: String){
        uiState = uiState.copy(amount = newValue)
    }

    fun onDateChange(newValue: Long){
        uiState = uiState.copy(date = newValue)
    }

    fun onGoalChange(newValue: Boolean){
        uiState = uiState.copy(isGoal = newValue)
        if (newValue){
            uiState = uiState.copy(
                categories = Categories.expensesCategory + Categories.otherCategory[0]
            )
        }
    }

    fun onCategoryIdChange(newValue:String){
        uiState = uiState.copy(categoryId = newValue)
    }

    fun onDescriptionChange(newValue:String){
        uiState = uiState.copy(description = newValue)
        if(newValue == "Внутрішній"){
            uiState = uiState.copy(
                walletIdTo = uiState.wallets[1].id
            )
        }
    }

    fun onTypeChange(newValue: TransactionType){
        val newCategories = when(newValue){
            TransactionType.Expense ->  if(uiState.isGoal) Categories.expensesCategory + Categories.otherCategory[0] else Categories.expensesCategory
            TransactionType.Income -> Categories.incomeCategory
            TransactionType.Transfer -> listOf(Categories.otherCategory[1])
        }

        uiState = uiState.copy(
            type = newValue,
            categories = newCategories,
            categoryId = newCategories.first().id,
            description = ""
        )
    }

    fun onWalletIdChange(newValue: String){
        uiState = uiState.copy(walletId = newValue)
    }

    fun onWalletIdToChange(newValue: String){
        uiState = uiState.copy(walletIdTo = newValue)
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    private fun loadWallets(){
        repository.getWallets(
            onUpdate = { newWallets ->
                uiState = if (newWallets.isNotEmpty()) {
                    uiState.copy(
                        wallets = newWallets.filter { it.id!="mono" },
                        walletId = newWallets.first().id
                    )
                } else {
                    uiState.copy(error = "У вас немає гаманців")
                }
            },
            onError = {newError ->
                uiState = uiState.copy(error = newError)
            }
        )
    }


    fun addTransaction() {
        viewModelScope.launch {

            // Валідація даних
            if (uiState.amount.isBlank() || uiState.amount.toDoubleOrNull() == null || uiState.amount.toDoubleOrNull() == 0.0) {
                uiState = uiState.copy(error = "Введіть коректну суму")
                return@launch
            }

            uiState = uiState.copy(isLoading = true, success = false, error = null)

            if(uiState.type == TransactionType.Expense){
                uiState = uiState.copy(amount = "-"+uiState.amount)
            }
            val transaction = Transaction(
                "",
                amount = uiState.amount.toDouble(),
                categoryId = uiState.categoryId,
                description = uiState.description,
                date = uiState.date,
                type = uiState.type,
                walletId = uiState.walletId,
                walletIdTo = uiState.walletIdTo
            )

            val result = addTransactionUseCase.execute(transaction)

            uiState = when (result) {
                is AddTransactionUseCase.Result.Success -> uiState.copy(
                    isLoading = false,
                    success = true,
                    error = null
                )

                is AddTransactionUseCase.Result.Failure -> uiState.copy(
                    isLoading = false,
                    success = false,
                    error =  result.message
                )

            }
        }
    }
}