package com.example.moneypath.domain.usecase.business

import android.util.Log
import com.example.moneypath.data.local.PrefsHelper
import com.example.moneypath.data.models.BudgetPlanRequest
import com.example.moneypath.data.models.BudgetPlanResponse
import com.example.moneypath.data.models.BudgetPlanResponseSimple
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.FixedExpenses
import com.example.moneypath.data.models.SettingsDB
import com.example.moneypath.data.models.toDb
import com.example.moneypath.data.repository.BackendRepository
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.ui.viewmodel.FormScreenViewModel
import com.example.moneypath.domain.usecase.crypto.GetWalletBalanceUseCase
import com.example.moneypath.utils.calculatePlanDates
import com.example.moneypath.utils.getTodayDate
import javax.inject.Inject
import kotlin.math.roundToInt

/*
    Use Case - оптимізація (створення плану)
    Включає FirebaseRepository та BackendRepository
    1. Зберігаємо елементи налаштування плану в firebase
    2. Через Апі здійснюємо оптимізацію (рахуємо поточний план і додаткові)
    3. Зберігаємо поточний план та додаткові в Firebase
 */

class OptimizeUseCase @Inject constructor(
    private val firebaseRepository:FirebaseRepository,
    private val backendRepository: BackendRepository,
    private val useCase: GetWalletBalanceUseCase,
    private val helper: PrefsHelper
){
    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(state: FormScreenViewModel.UiState): Result {
        return try {
            // Видаляємо попередній план
            firebaseRepository.deleteUserPlanning()
            firebaseRepository.deleteTransactionsByCategory("goal")
            helper.clearAll()
            // Зберігаємо імя та суму цілі якщо вона є в sharedpref
            if(state.isGoal && state.goalAmount!= null && state.goalName != null){
                helper.saveGoalName(state.goalName)
                helper.saveGoalAmount(state.goalAmount)
            }

            // 1. Обраховуємо баланс користувача на обраних гаманцях
            var userBalance = 0.0
            state.wallets.forEach { wallet ->
                val walletBalance = useCase(walletId = wallet.id)
                if (walletBalance != null) {
                    userBalance += walletBalance
                }
            }
            // перетворюєм назви категорій в айді
            val categories = state.categories.map {name ->
                Categories.expensesCategory.find { name == it.name }?.id ?: ""
            }
            val fixedCategories = state.fixedCategories.map{name ->
                Categories.expensesCategory.find { name == it.name }?.id ?:""
            }

            // 2. Створюємо об'єкт для запиту до апі
            val request = BudgetPlanRequest(
                // плюс поточний баланс
                balance = state.currentIncome.toDouble() + userBalance,
                bounds = state.bounds.map { listOf(it.first, it.second) },
                income = state.stableIncome,
                categories = categories,
                fixed_expenses = FixedExpenses(
                    state.fixedAmountStable.sum(),
                    state.fixedAmountCurrent.sum()
                ),
                priorities = state.priorities,
                months = state.months,
                goal = state.goalAmount,
            )

            // 3. Створюємо об'єкт налаштувань та зберігаємо в firebase
            val settings = SettingsDB(
                goal = state.isGoal,
                months = state.months,
                goal_name = state.goalName,
                goal_amount = state.goalAmount,
                fixed_categories = fixedCategories,
                fixed_amount_stable = state.fixedAmountStable,
                fixed_amount_current = state.fixedAmountCurrent,
                categories = categories,
                bounds = state.bounds.map { listOf(it.first, it.second) },
                priorities = state.priorities,
                stable_income = state.stableIncome,
                current_income = state.currentIncome,
                wallets = state.wallets.map { it.id }.toList(),
                min_month = state.minMonth
            )
            Log.d("OptimizeUseCase", state.wallets.map { it.id }.toList().toString())
            if (!firebaseRepository.updateSetup(settings)) throw Exception("Дані не збереглися")

            // 4. Обраховуємо кінець поточного і стабільного плану і початок плану
            val(currentPlanEnd, stablePlanEnd) = calculatePlanDates(state.months)
            val planStart = getTodayDate()

            // 5. Обраховуємо поточний план
            val result: Any? = if (state.isGoal) {
                backendRepository.getOptimizedPlanGoal(request)
            } else {
                backendRepository.getOptimizedPlanSimple(request)
            }
            // 6. Зберігаємо поточний план в бд
            val fixedCurrentMap = fixedCategories.zip(state.fixedAmountCurrent.map { it.toDouble() }).toMap()
            val fixedStableMap = fixedCategories.zip(state.fixedAmountStable.map { it.toDouble() }).toMap()
            Log.d("OptimizeUseCase", result.toString())
            if(result!=null){
                when(result){
                    is BudgetPlanResponse -> firebaseRepository.updateTotalPlan(
                        result.toDb(
                            currentEnd = currentPlanEnd,
                            stableEnd =  stablePlanEnd,
                            planStart = planStart,
                            months = state.months,
                            currentFixed = fixedCurrentMap,
                            stableFixed = fixedStableMap,
                            income = state.stableIncome
                        )
                    )
                    is BudgetPlanResponseSimple -> firebaseRepository.updateTotalPlan(
                        result.toDb(
                            currentEnd = currentPlanEnd,
                            planStart = planStart,
                            currentFixed = fixedCurrentMap,
                            stableFixed = fixedStableMap,
                            income = state.stableIncome
                        )
                    )
                }
            }else throw Exception("Оптимізація не вдалася")

            // 7. Розраховуємо та зберігаємо додаткові плани
            if(state.isGoal && state.months!=null){
                val term1 = if(state.months != state.minMonth) state.minMonth else state.months.times(1.3).roundToInt()
                val term2 = state.months.times(1.5).roundToInt()
                val term3 = state.months.times(2)
                val terms = listOf(term1, term2, term3).distinct().filter { it!! <=60 }

                terms.forEachIndexed{index, term->
                    val requestWithNewMonths = request.copy(
                        months = term
                    )
                    val addResult = backendRepository.getOptimizedPlanGoal(requestWithNewMonths)
                    if(addResult != null){
                        firebaseRepository.addAdditionalPlan(addResult.toDb(
                            planStart = planStart,
                            currentEnd = null,
                            stableEnd = null,
                            months = term,
                            currentFixed = fixedCurrentMap,
                            stableFixed = fixedStableMap,
                            income = state.stableIncome
                        ), index )
                    }else throw Exception("Оптимізація додаткових планів не вдалася")
                }
            }
            Result.Success
        }catch (e:Exception){
            Log.d("OptimizeUseCase", "${e.message}")
            Result.Failure("Не вдалось розрахувати план. Спробуйте ще раз")
        }
    }
}
