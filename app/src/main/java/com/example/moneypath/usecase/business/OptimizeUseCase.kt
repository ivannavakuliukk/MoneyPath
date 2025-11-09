package com.example.moneypath.usecase.business

import android.util.Log
import com.example.moneypath.data.models.BudgetPlanDB
import com.example.moneypath.data.models.BudgetPlanRequest
import com.example.moneypath.data.models.BudgetPlanResponse
import com.example.moneypath.data.models.BudgetPlanResponseSimple
import com.example.moneypath.data.models.Categories
import com.example.moneypath.data.models.FixedExpenses
import com.example.moneypath.data.models.Transaction
import com.example.moneypath.data.models.toDb
import com.example.moneypath.data.repository.BackendRepository
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.ui.viewmodel.FormScreenViewModel
import com.example.moneypath.usecase.crypto.GetWalletBalanceUseCase
import com.example.moneypath.utils.calculatePlanDates
import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.roundToInt

/*
    Включає FirebaseRepository та OptimizeRepository
    1. Зберігаємо елементи налаштування плану в firebase
    2. Через Апі здійснюємо оптимізацію (рахуємо поточний план і додаткові)
    3. Зберігаємо поточний план та додаткові в Firebase
 */

class OptimizeUseCase @Inject constructor(
    private val firebaseRepository:FirebaseRepository,
    private val backendRepository: BackendRepository,
    private val useCase: GetWalletBalanceUseCase
){
    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(state: FormScreenViewModel.UiState): Result {
        return try {
            // Видаляємо попередній план
            firebaseRepository.deleteUserPlanning()
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

            // 2. Створюємо об'єкт налаштування плану
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
                fixed_categories = fixedCategories,
                fixed_amount_stable = state.fixedAmountStable,
                fixed_amount_current = state.fixedAmountCurrent
            )

            // 3. Зберігаємо налаштування плану в firebase
            if (!firebaseRepository.updateSetup(request)) throw Exception("Дані не збереглися")

            // 4. Обраховуємо кінець поточного і стабільного плану
            val(current_plan_end, stable_plan_end) = calculatePlanDates(state.months)

            // 5. Обраховуємо поточний план
            var result: Any? = null
            result = if (state.isGoal) {
                backendRepository.getOptimizedPlanGoal(request)
            } else {
                backendRepository.getOptimizedPlanSimple(request)
            }
            // 6. Зберігаємо поточний план в бд
            if(result!=null){
                when(result){
                    is BudgetPlanResponse -> firebaseRepository.updateTotalPlan(
                        result.toDb(currentEnd = current_plan_end, stableEnd =  stable_plan_end, months = state.months)
                    )
                    is BudgetPlanResponseSimple -> firebaseRepository.updateTotalPlan(
                        result.toDb(currentEnd = current_plan_end)
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
                        firebaseRepository.addAdditionalPlan(addResult.toDb(null, null, term), index )
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
