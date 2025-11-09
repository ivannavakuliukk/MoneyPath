package com.example.moneypath.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneypath.data.models.Wallet
import com.example.moneypath.data.repository.BackendRepository
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.business.OptimizeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormScreenViewModel @Inject constructor(
    private val useCase: OptimizeUseCase,
    private val firebaseRepository: FirebaseRepository,
    private val backendRepository: BackendRepository
): ViewModel(){
    data class UiState(
        val currentStep: Int = 0,
        val isGoal:Boolean = true,
        val goalName: String? = null,
        val goalAmount: Int? = null,
        val categories: List<String> = emptyList(),
        val newOrderCategories: List<String> = emptyList(),
        val priorities: List<Int> = emptyList(),
        val bounds: List<Pair<Int, Int>> = emptyList(),
        val fixedCategories: List<String> = emptyList(),
        val fixedAmountCurrent: List<Int> = emptyList(),
        val fixedAmountStable: List<Int> = emptyList(),
        val months: Int? = null,
        val userWallets: List<Wallet> = emptyList(),
        val wallets: List<Wallet> = emptyList(),
        val stableIncome: Int = 0,
        val currentIncome: Int = 0,
        val minMonth:Int? = null,

        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())

    init {
        loadWallets()
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    private fun nextStep() {
        val step = uiState.currentStep
        val isGoal = uiState.isGoal
        var next = step

        if (!isGoal) {
            next = when {
                step == 0 ->  2
                step == 7 -> 9
                step == 5 && uiState.fixedCategories.isEmpty() -> step + 2
                step < 9 -> step + 1
                else -> step
            }
        } else {
            next = when {
                step == 5 && uiState.fixedCategories.isEmpty() -> step + 2
                step < 9 -> step + 1
                else -> step
            }
        }
        uiState = uiState.copy(currentStep = next)
    }

    fun previousStep(){
        val step = uiState.currentStep
        val isGoal = uiState.isGoal
        var prev = step

        if (!isGoal) {
            prev = when {
                step == 2 ->  0
                step == 7 && uiState.fixedCategories.isEmpty() -> step - 2
                step == 9 -> 7
                step > 0 -> step - 1
                else -> step
            }
        } else {
            prev = when {
                step == 7 && uiState.fixedCategories.isEmpty() -> step -2
                step > 0 -> step - 1
                else -> step
            }
        }
        uiState = uiState.copy(currentStep = prev)
    }

    // Перевірки при переході на наступний крок
    fun onNextClick(){
        viewModelScope.launch {
            when (uiState.currentStep) {
                0 -> {}
                1 -> {
                    checkGoalName()
                    checkGoalAmount()
                }
                2 -> {
                    checkStableIncome()
                    checkCurrentIncome()
                }
                3 -> checkCategories()
                4 -> checkAndModifyBounds()
                5 -> updatePriorities()
                6 -> checkFixedAmountCurrent()
                7 -> checkWallets()
                8 -> checkMonths()
            }
            if (uiState.currentStep == 9) {
                uiState = uiState.copy(isLoading = true)
                val result = useCase.execute(uiState)
                uiState = when (result) {
                    is OptimizeUseCase.Result.Success ->
                        uiState.copy(isSuccess = true, isLoading = false)

                    is OptimizeUseCase.Result.Failure ->
                        uiState.copy(error = result.message, isLoading = false)
                }
            }
            if (uiState.error == null) nextStep()
        }
    }

    // Окремо функції ініціалізації та перевірок(якщо потрібно) при натисненні на "Далі"
    //Ціль
    fun updateIsGoal(isGoal: Boolean){
        uiState = uiState.copy(isGoal = isGoal)
    }

    fun updateGoalName(goalName: String?){
        uiState = if(uiState.isGoal){
            uiState.copy(goalName = goalName)
        }else{
            uiState.copy(goalName = null)
        }
    }

    private fun checkGoalName(){
        if(uiState.goalName != null){
            if(uiState.goalName!!.length<=3 || uiState.goalName!!.length >= 50){
                uiState =uiState.copy(error = "Введіть коректну назву мети (від 3 до 50 символів)")
            }
        }else{
           uiState= uiState.copy(error = "Введіть коректну назву мети")
        }
    }

    fun updateGoalAmount(goalAmount: Int?){
        uiState = if(uiState.isGoal){
            uiState.copy(goalAmount = goalAmount)
        }else{
            uiState.copy(goalAmount = null)
        }
    }

    private fun checkGoalAmount(){
        if(uiState.goalAmount != null){
            if(uiState.goalAmount!! <= 1000){
                uiState =uiState.copy(error = "Введіть коректну суму(мін.1000)")
            }
        }else {
            uiState = uiState.copy(error = "Введіть коректну суму(мін.1000)")
        }
    }

    // Дохід
    fun updateCurrentIncome(currentIncome: Int){
        uiState = uiState.copy(currentIncome = currentIncome)
    }

    private fun checkCurrentIncome(){
        if(uiState.currentIncome < 0){
           uiState= uiState.copy(error = "Дохід не може бути від'ємним")
        }
    }

    fun updateStableIncome(stableIncome: Int){
        uiState = uiState.copy(stableIncome = stableIncome)
    }

    private fun checkStableIncome(){
        if(uiState.stableIncome <=1000){
            uiState = uiState.copy(error = "Стабільний дохід не може бути меншим 1000")
        }
        Log.d("FormScreenViewModel", "step = ${uiState.currentStep}")
    }

    // Категорії
    // Перемикач
    fun toggleCategory(category: String){
        val current = uiState.categories.toMutableList()
        if(current.contains(category)) current.remove(category) else current.add(category)
        uiState = uiState.copy(categories = current)
    }

    private fun checkCategories(){
        if(uiState.categories.size>=12){
            uiState = uiState.copy(error = "Можна обрати не більше 12 категорій") }
        if(uiState.categories.size < 5){
            uiState = uiState.copy(error = "Оберіть хоча б 5 категорій")
        }
    }

    // Межі
    fun updateBoundForCategory(index:Int, low: Int?, high: Int?){
        val currentBounds = uiState.bounds.toMutableList()
        // Якщо не ініціалізовано bounds - створюємо за замовчуванням
        while (currentBounds.size<uiState.categories.size){
            currentBounds.add(0 to 0)
        }
        // Оновлюємо конкретну пару для обраної категорії
        val safeLow = low ?: 0
        val safeHigh = high ?: 0
        currentBounds[index] = safeLow to safeHigh
        uiState = uiState.copy(bounds = currentBounds)
    }

    private suspend fun checkAndModifyBounds(){
        // знаходимо індекси елементів де межі однакові
        val equalIndexes = uiState.bounds.mapIndexedNotNull { index, (low, high) ->
            if (low == high) index else null
        }
        if(uiState.bounds.any{it.first < 0 || it.second <0}){
            uiState = uiState.copy(error = "Межі не можуть бути менші 0")
        }else if(uiState.bounds.any{it.first>it.second}){
            uiState = uiState.copy(error = "Верхня межа не може бути менша нижньої")
        }else if(uiState.bounds.any{ it.second == 0}){
            uiState = uiState.copy(error = "Верхня межа не може бути 0.")
        } else {
            if (equalIndexes.isNotEmpty()) {
                uiState = uiState.copy(
                    // Ініціалізуємо фіксовані категорії та значення
                    fixedAmountStable = equalIndexes.map { uiState.bounds[it].first },
                    fixedCategories = equalIndexes.map { uiState.categories[it] },
                    // Коригуємо категорії та межі щоб вони були без фіксованих
                    bounds = uiState.bounds.filterIndexed { index, _ -> index !in equalIndexes },
                    categories = uiState.categories.filterIndexed { index, _ -> index !in equalIndexes }
                )
            }
        }
        //findminmonths і перевірка і ініціалізація minMonth
        findMinMonths()
        if (uiState.minMonth == null) {
            uiState = uiState.copy(
                error = "З такими даними неможливо спланувати бюджет. Перевірте свої нижні межі, можливо вони перевищують дохід."
            )
        }

    }

    // Пріоритети
    fun updateNewOrderCategories(newOrderCategories: List<String>){
        uiState = uiState.copy(newOrderCategories = newOrderCategories)
    }

    private fun updatePriorities(){
        if(uiState.newOrderCategories.isEmpty()){
            uiState = uiState.copy(newOrderCategories = uiState.categories)
        }
        val size = uiState.newOrderCategories.size
        // Шукаємо індекси переставлених категорій та робимо з них пріоритети:
        // чим менше від'ємне число - це важливіша категорія
        val priorities = uiState.categories.map {
            category-> uiState.newOrderCategories.indexOf(category) - size
        }
        uiState = uiState.copy(priorities = priorities)
        Log.d("FormScreenViewModel", "priorities: $priorities \n categories: ${uiState.categories} \n neworder: ${uiState.newOrderCategories}")

    }
    // Межі цього місяця
    fun updateFixedAmountCurrent(index:Int, amount: Int?){
       val currentAmounts = uiState.fixedAmountCurrent.toMutableList()
        while(currentAmounts.size < uiState.fixedAmountStable.size){
            currentAmounts.add(0)
        }
        currentAmounts[index] = amount ?: 0
        uiState = uiState.copy(fixedAmountCurrent = currentAmounts)
    }

    private fun checkFixedAmountCurrent(){
        if(uiState.fixedAmountCurrent.any{it<0}){
            uiState = uiState.copy(error = "Лише позитивне значення")
        }else if(uiState.fixedAmountCurrent.zip(uiState.fixedAmountStable).any { (current, stable)->
            current>stable }){
            uiState = uiState.copy(error = "Значення не може бути більшим ніж зазначені раніше фіксовані витрати")
        }
    }

    private fun loadWallets(){
        firebaseRepository.getWallets(
            onUpdate = { newWallets ->
                uiState = if (newWallets.isNotEmpty()) {
                    uiState.copy(userWallets = newWallets)
                } else {
                    uiState.copy(error = "У вас немає гаманців")
                }
            },
            onError = {newError -> uiState = uiState.copy(error = newError) }
        )
    }

    fun updateWallets(wallet: Wallet){
        val current = uiState.wallets.toMutableList()
        if(current.contains(wallet)) current.remove(wallet)
        else current.add(wallet)
        uiState = uiState.copy(wallets = current)
    }

    private fun checkWallets(){
        if(uiState.wallets.isEmpty()){
            uiState = uiState.copy(error = "Оберіть хоча б один гаманець")
        }
    }

    // Місяці
    fun updateMonths(months: Int?){
        uiState = uiState.copy(months = months)
    }

    private fun checkMonths(){
        if (uiState.months != null ) {
            if(uiState.months!! < uiState.minMonth!! || uiState.months!! >= 60){
                uiState = uiState.copy(error = "Введіть коректну кількість місяців (не більше 60, не менше мін. значення)")
            }

        }else uiState =uiState.copy(error = "Введіть коректну кількість місяців")
    }


    private suspend fun findMinMonths(){
         try {
            val response = backendRepository.getMinMonths(
                income = uiState.stableIncome,
                goal = uiState.goalAmount ?: 0,
                bounds = uiState.bounds.map { listOf( it.first, it.second) },
                fixed_expenses = uiState.fixedAmountStable.sum()
            )
             if (response != null) {
                 uiState = uiState.copy(minMonth = response.min_months)
             }
            Log.d("FormScreenViewModel", "minMonths: ${uiState.minMonth}")
        } catch (e: Exception) {
            Log.d("FormScreenViewModel", "${e.message}")
        }
    }
    
//    private suspend fun optimize(){
//        try{
//            val result = backendRepository.getOptimizedPlanGoal(
//                BudgetPlanRequest(
//                    // плюс поточний баланс
//                    balance = uiState.currentIncome.toDouble(),
//                    bounds = uiState.bounds.map { listOf( it.first, it.second) },
//                    income = uiState.stableIncome,
//                    categories = uiState.categories,
//                    fixed_expenses = FixedExpenses(uiState.fixedAmountStable.sum(), uiState.fixedAmountCurrent.sum()),
//                    priorities = uiState.priorities,
//                    months = uiState.months,
//                    goal = uiState.goalAmount
//                )
//            )
//            if (result != null) {
//                Log.d("FormScreenViewModel", "${result.current_month}")
//            }
//        } catch (e: Exception) {
//            Log.d("FormScreenViewModel", "${e.message}")
//        }
//    }
//
//    private suspend fun optimizeSimple(){
//        try{
//            val result = backendRepository.getOptimizedPlanSimple(
//                BudgetPlanRequest(
//                    // плюс поточний баланс
//                    balance = uiState.currentIncome.toDouble(),
//                    bounds = uiState.bounds.map { listOf( it.first, it.second) },
//                    income = uiState.stableIncome,
//                    categories = uiState.categories,
//                    fixed_expenses = FixedExpenses(uiState.fixedAmountStable.sum(), uiState.fixedAmountCurrent.sum()),
//                    priorities = uiState.priorities,
//                    months = null,
//                    goal = null
//                )
//            )
//            if (result != null) {
//                Log.d("FormScreenViewModel", "${result.current_month}")
//            }
//        } catch (e: Exception) {
//            Log.d("FormScreenViewModel", "${e.message}")
//        }
//    }



}
