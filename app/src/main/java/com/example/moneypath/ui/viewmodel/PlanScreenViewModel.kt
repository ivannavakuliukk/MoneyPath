package com.example.moneypath.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneypath.data.local.PrefsHelper
import com.example.moneypath.data.models.BudgetPlanDB
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.SettingsDB
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.domain.usecase.business.SelectAdditionalPlanUseCase
import com.example.moneypath.utils.TimeDiff
import com.example.moneypath.utils.calculateMonthsAndDaysBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PlanScreenViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val helper: PrefsHelper,
    private val useCase: SelectAdditionalPlanUseCase
): ViewModel() {
    data class UiState(
        val isPlanned: Boolean = false,
        val isGoal: Boolean = true,
        val isWallets: Boolean = false,
        val plan: BudgetPlanDB? = null,
        val goalName: String? = null,
        val goalAmount: Int? = null,
        val additionalPlans: List<BudgetPlanDB> = emptyList(),
        val fullCategoriesMap: Map<String, Double> = emptyMap(),
        val setUp: SettingsDB? = null,
        val period: TimeDiff? = null,
        val walletNames: List<String>? = null,

        val error: String? = null,
        val isLoading:Boolean = true,
        val reloadNeeded: Boolean = false
    )

    var uiState by mutableStateOf(UiState())

    init {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val planDeferred = async { updatePlan() }
            val additionalPlansDeferred = async { updateAdditionalPlans() }
            val walletsDeferred = async { checkWallets() }

            planDeferred.await()
            additionalPlansDeferred.await()
            walletsDeferred.await()
            getFullCategoriesMap()
            getTimeDiff()


            uiState = uiState.copy(isLoading = false)
        }
    }

    private suspend fun updatePlan(){
        val plan = repository.getTotalPlan()
        if(plan != null) {
            uiState = uiState.copy(
                isPlanned = true, plan = plan, isGoal = plan.goal,
                goalName = helper.getGoalName(),
                goalAmount = helper.getGoalAmount()
            )
        }else{
            uiState = uiState.copy(isPlanned = false)
        }

    }

    private suspend fun updateAdditionalPlans(){
        val plans = repository.getAdditionalPlans()
        if(plans.isNotEmpty()){
            uiState = uiState.copy(additionalPlans = plans)
        }
    }

    private fun getFullCategoriesMap(){
        uiState.plan?.let { plan ->
            // Дістаємо всі категорії
            val fixedCategories: Map<String, Double> = plan.stable_fixed
            val categoriesMap: Map<String, Double> = plan.stable_months_allocation
            val goalMap: Map<String, Double> = if(uiState.isGoal && uiState.plan?.stable_savings != null){
                mapOf("Відкладення на ціль" to  (uiState.plan!!.stable_savings?: 0.0))
            }else emptyMap()

            // Сортуємо за спаданням та додаємо ціль наперед(якщо вона є)

            val combinedMap: Map<String, Double> = fixedCategories + categoriesMap + goalMap
            val sortedMap: List<Pair<String, Double>> = combinedMap
                .toList()
                .sortedByDescending { it.second }

            val fullCategories = linkedMapOf<String, Double>().apply {
                goalMap.forEach { (key, value) -> put(key, value) }
                sortedMap.forEach { (key, value) ->
                    val newKey = Categories.expensesCategory.find { it.id == key }?.name ?: key
                    put(newKey, value)
                }
            }
            uiState = uiState.copy(fullCategoriesMap = fullCategories)
            Log.d("PlanScreenViewModel", uiState.fullCategoriesMap.keys.toString())
        }
    }

    fun getSetUp(){
        viewModelScope.launch {
            getSetUpSuspend()
        }
    }

    private suspend fun getSetUpSuspend(){
        if(uiState.setUp == null) {
            var setUp = repository.getSetup()
            if (setUp != null) {
                val walletNames = setUp.wallets.map{ walletId->
                    repository.getWalletById(walletId)?.name ?: ""
                }
                uiState = uiState.copy(setUp = setUp, walletNames = walletNames)
            }
        }
    }

    fun onErrorChange(error: String?){
        uiState = uiState.copy(error = error)
    }

    fun clearError(){
        uiState = uiState.copy(error = null)
    }

    private suspend fun checkWallets(){
        val result = repository.checkIfWalletsExist()
        uiState = uiState.copy(isWallets = result)
    }

    fun deletePlan(){
        repository.deleteUserPlanning()
        helper.clearAll()
        uiState = uiState.copy(isPlanned = false)
    }

    fun getTimeDiff(){
        uiState.plan?.let { plan ->
            val startTime = Calendar.getInstance().timeInMillis
            val endTime = plan.stable_plan_end
            val period = endTime?.let { calculateMonthsAndDaysBetween(startTime, it) }
            uiState = uiState.copy(period = period)
        }
    }

    fun selectAdditionalPlan(index:Int){
        viewModelScope.launch {
            if(uiState.setUp == null){
                getSetUpSuspend()
            }
            if (uiState.setUp != null && uiState.additionalPlans.isNotEmpty()) {
                Log.d("PlanScreenViewModel", "i am working")
                uiState = uiState.copy(isLoading = true)
                val month = uiState.additionalPlans[index].months
                val result = useCase.execute(uiState.setUp!!, month!!)
                uiState = when (result) {
                    is SelectAdditionalPlanUseCase.Result.Success ->
                        uiState.copy(reloadNeeded = true)

                    is SelectAdditionalPlanUseCase.Result.Failure ->
                        uiState.copy(error = result.message, isLoading = false)
                }
            }
        }
    }
}