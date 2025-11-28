package com.example.moneypath.usecase.business

import android.util.Log
import com.example.moneypath.data.models.SettingsDB
import com.example.moneypath.data.models.toDb
import com.example.moneypath.data.models.toRequest
import com.example.moneypath.data.repository.BackendRepository
import com.example.moneypath.data.repository.FirebaseRepository
import com.example.moneypath.usecase.crypto.GetWalletBalanceUseCase
import com.example.moneypath.utils.calculatePlanDates
import com.example.moneypath.utils.getTodayDate
import javax.inject.Inject
import kotlin.math.roundToInt

/*
    Включає FirebaseRepository та BackendRepository
    1. З наявних елементів налаштування в Firebase робимо новий запит до апі,
        але змінюємо к-сть місяців та баланс.
    2. Надсилаємо запит до апі для обрахування нового плану та нових трьох додаткових.
    3. Зберігаємо новий поточний та додавтовий плани + змінені налаштування в firebase
 */

class SelectAdditionalPlanUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val backendRepository: BackendRepository,
    private val useCase: GetWalletBalanceUseCase
){
    sealed class Result {
        object Success: Result()
        data class Failure(val message: String): Result()
    }

    suspend fun execute(settings: SettingsDB, months: Int): Result {
        return try {
            // 1. Видаляємо попередній план(але не видаляємо prefs, бо вони такі ж)
            firebaseRepository.deleteUserPlanning()

            // 2.Рахуємо баланс на гаманцях
            var userBalance = 0.0
            settings.wallets.forEach { walletId ->
                val walletBalance = useCase(walletId = walletId)
                if (walletBalance != null) {
                    userBalance += walletBalance
                }
            }
            // 3. Робимо нові моделі: запит до апі та змінюємо налаштування
            val request = settings.toRequest(balance = userBalance, months = months)
            Log.d("SelectAdditionalPlanUseCase", request.toString())
            val newSettings = settings.copy(months = months)
            if (!firebaseRepository.updateSetup(newSettings)) throw Exception("Дані не збереглися")

            // 4. Обраховуємо кінець поточного і стабільного плану і початок плану
            val(currentPlanEnd, stablePlanEnd) = calculatePlanDates(months)
            val planStart = getTodayDate()

            // 5. Обраховуємо новий поточний план
            val result = backendRepository.getOptimizedPlanGoal(request)

            // 6. Зберігаємо поточний план в бд
            val fixedCurrentMap = settings.fixed_categories.zip(settings.fixed_amount_current.map { it.toDouble() }).toMap()
            val fixedStableMap = settings.fixed_categories.zip(settings.fixed_amount_stable.map { it.toDouble() }).toMap()
            if(result!=null){
                firebaseRepository.updateTotalPlan(
                    result.toDb(
                        currentEnd = currentPlanEnd,
                        stableEnd =  stablePlanEnd,
                        planStart = planStart,
                        months = months,
                        currentFixed = fixedCurrentMap,
                        stableFixed = fixedStableMap,
                        income = settings.stable_income
                    )
                )
            }else throw Exception("Оптимізація не вдалася")

            // 7. Розраховуємо та зберігаємо додаткові плани
            val term1 = if(months != settings.min_month) settings.min_month else months.times(1.3).roundToInt()
            val term2 = months.times(1.5).roundToInt()
            val term3 = months.times(2)
            val terms = listOf(term1, term2, term3).distinct().filter { it!! <=60 }

            terms.forEachIndexed{index1, term->
                val requestWithNewMonths = request.copy(
                    months = term
                )
                Log.d("SelectAdditionalPlanUseCase", requestWithNewMonths.toString())
                val addResult = backendRepository.getOptimizedPlanGoal(requestWithNewMonths)
                if(addResult != null){
                    firebaseRepository.addAdditionalPlan(addResult.toDb(
                        planStart = planStart,
                        currentEnd = null,
                        stableEnd = null,
                        months = term,
                        currentFixed = fixedCurrentMap,
                        stableFixed = fixedStableMap,
                        income = settings.stable_income
                    ), index1 )
                }else throw Exception("Оптимізація додаткових планів не вдалася")
            }
            Result.Success
        }catch (e:Exception){
            Log.d("SelectAdditionalPlanUseCase", "${e.message}")
            Result.Failure("Не вдалось розрахувати план. Спробуйте ще раз")
        }

    }
}