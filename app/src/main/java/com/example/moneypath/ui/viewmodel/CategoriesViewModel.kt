package com.example.moneypath.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.PieSlice
import com.example.moneypath.domain.models.Transaction
import com.example.moneypath.data.models.findCategoryById
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.domain.usecase.business.GetTransactionsByCategories
import com.example.moneypath.utils.getMonthBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val useCase:GetTransactionsByCategories
): ViewModel() {
    data class UiState(
        val isPlanned: Boolean = false,
        val isGoal: Boolean = false,
        val isStable: Boolean = false,
        val planAllocation: Map<String, Double> = emptyMap(),
        val wallets: List<String>? = null,
        val categoryList: List<String> = emptyList(),
        val transactionList: Map<String, List<Transaction>> = emptyMap(),
        val dates: Pair<Long, Long> ? = null,

        //Дані для діаграм
        val categoriesChartData: List<PieSlice> = emptyList(),
        val planChartData: List<PieSlice> = emptyList(),

        // Витрати і доходи
        val expensesAmount: Double = 0.0,
        val incomeAmount: Double = 0.0,

        val isLoading: Boolean = false,
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())

    init {
       updateAllData()
    }

    private suspend fun updatePlan() :Boolean {
        val plan = repository.getTotalPlan()

        val (startDate, endDate) = uiState.dates ?: getMonthBounds()

        if (plan == null || endDate * 1000 < plan.plan_start) {
            // Місяць до створення плану або плану немає
            uiState = uiState.copy(
                isPlanned = false,
                categoryList = Categories.expensesCategory.map { it.id },
                dates = Pair(startDate, endDate),
                planAllocation = emptyMap()
            )
            return false
        }

        uiState = uiState.copy(
            isPlanned = true,
            isGoal = plan.goal
        )

        val selectedMonthEndMillis = endDate * 1000
        val selectedMonthStartMillis = startDate * 1000
        val currentPlanEndMillis = plan.current_plan_end ?: 0L
        val isStable = selectedMonthEndMillis > currentPlanEndMillis
        // Якщо обрали місяць поточного плану, початок місяця = план старт
        val effectiveMonthStartMillis = if (!isStable && selectedMonthStartMillis < plan.plan_start) {
            plan.plan_start
        } else {
            selectedMonthStartMillis
        }

        val monthAllocation =
            if (isStable) plan.stable_months_allocation else plan.current_month_allocation
        val fixed =
            if (isStable) plan.stable_fixed else plan.current_fixed
        val savings =
            (if (isStable) plan.stable_savings else plan.current_savings) ?: 0.0

        val planAllocation =
            if (uiState.isGoal) monthAllocation + fixed + mapOf("goal" to savings)
            else monthAllocation + fixed
        val categoryList =
            if (uiState.isGoal) monthAllocation.keys + fixed.keys + "goal"
            else monthAllocation.keys + fixed.keys

        uiState = uiState.copy(
            isStable = isStable,
            planAllocation = planAllocation,
            categoryList = categoryList.toList(),
            dates = Pair(effectiveMonthStartMillis/1000, endDate)
        )
        return true
    }


    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    fun loadChartData(planNeeded: Boolean){
        val transactionListNotNull = uiState.transactionList.filter { it.value.sumOf { it.amount } != 0.0 }
        val categories = transactionListNotNull.keys.map { findCategoryById(it) }
        val values = transactionListNotNull.values.map { it.sumOf { t -> t.amount } }
        val primaryColor = Color.White

        val chartData = categories.mapIndexed { index, category ->
            PieSlice(
                value = values[index],
                color = category.color ?: primaryColor,
                icon = category.iconRes
            )
        }
        uiState = uiState.copy(categoriesChartData = chartData)
        if(planNeeded) {
            if (uiState.planAllocation.isNotEmpty()) {
                val plan = uiState.planAllocation
                val planNotNull = plan.filter { it.value != 0.0 }
                val planCategories = planNotNull.keys.map { findCategoryById(it) }.toMutableList()
                val planValues = planNotNull.values.toMutableList()
                val planChartData = planCategories.mapIndexed { index, category ->
                    PieSlice(
                        value = planValues[index],
                        color = category.color ?: primaryColor,
                        icon = category.iconRes
                    )
                }
                uiState = uiState.copy(planChartData = planChartData)
            }
        }else uiState = uiState.copy(planChartData = emptyList())
    }

    fun updateAllData(){
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val planNeeded= updatePlan()
            val result = useCase.execute(uiState.categoryList, uiState.dates ?: Pair(0L, 0L), uiState.wallets)
            when(result){
                is GetTransactionsByCategories.Result.Success -> {
                    uiState = uiState.copy(
                        transactionList = result.data,
                        expensesAmount = result.totalExpenses,
                        incomeAmount = result.totalIncome,
                        isLoading = false
                    )
                }
                is GetTransactionsByCategories.Result.Failure -> uiState = uiState.copy(
                    error = result.message, isLoading = false
                )
            }
            loadChartData(planNeeded)
        }
    }

    fun onDateRangeSelected(startDate: Long, endDate:Long){
        uiState = uiState.copy(dates = Pair(startDate, endDate))
        updateAllData()
    }
}