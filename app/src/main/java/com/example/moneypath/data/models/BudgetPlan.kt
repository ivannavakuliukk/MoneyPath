package com.example.moneypath.data.models

// Модель - запит до апі
data class BudgetPlanRequest(
    val balance: Double = 0.0,
    val income: Int = 0,
    val goal: Int? = null,
    val months: Int? = null,
    val fixed_expenses: FixedExpenses = FixedExpenses(),
    val categories: List<String> = emptyList(),
    val bounds: List<List<Int>> = emptyList(),
    val priorities: List<Double> = emptyList()
)

// Модель - відповідь (план з ціллю)
data class BudgetPlanResponse(
    val current_leftover: Double,
    val current_month: Map<String, Double>,
    val current_savings: Double,
    val goal_percent: Double,
    val stable_months: Map<String, Double>,
    val stable_leftover: Double,
    val stable_savings: Double,
    val error: String? = null
)

// Модель - відповідь (план без цілі)
data class BudgetPlanResponseSimple(
    val current_leftover: Double,
    val current_month: Map<String, Double>,
    val stable_months: Map<String, Double>,
    val stable_leftover: Double,
    val error: String? = null
)

data class FixedExpenses(
    val stable: Int = 0,
    val current: Int = 0
)

// Модель - план в бд
data class BudgetPlanDB(
    val goal: Boolean = false,
    val income: Int = 0,
    val months: Int? = null,
    val current_leftover: Double = 0.0,
    val current_month_allocation: Map<String, Double> = emptyMap(),
    val current_fixed: Map<String, Double> = emptyMap(),
    val current_savings: Double? = null,
    val goal_percent: Double? = null,
    val stable_months_allocation: Map<String, Double> = emptyMap(),
    val stable_fixed: Map<String, Double> = emptyMap(),
    val stable_leftover: Double = 0.0,
    val stable_savings: Double? = null,
    val plan_start: Long = 0L,
    val current_plan_end: Long? = null,
    val stable_plan_end: Long? = null
)

// Модель - налаштування плану в бд
data class SettingsDB(
    val goal: Boolean = false,
    val goal_amount: Int? = null,
    val months: Int? = null,
    val min_month: Int? = null,
    val goal_name: String? = null,
    val fixed_categories: List<String> = emptyList(),
    val fixed_amount_current: List<Int> = emptyList(),
    val fixed_amount_stable: List<Int> = emptyList(),
    val categories: List<String> = emptyList(),
    val bounds: List<List<Int>> = emptyList(),
    val priorities: List<Double> = emptyList(),
    val stable_income: Int = 0,
    val current_income: Int = 0,
    val wallets: List<String> = emptyList()
)

// Мапери для перетворення налаштування в запит

fun SettingsDB.toRequest(months: Int, balance: Double) = BudgetPlanRequest(
    balance = (balance + current_income),
    income = stable_income,
    goal = goal_amount,
    months = months,
    fixed_expenses = FixedExpenses(fixed_amount_stable.sum(), fixed_amount_current.sum()),
    categories = categories,
    bounds = bounds,
    priorities = priorities
)



// Мапери для перетворення відповіді в модель db
fun BudgetPlanResponse.toDb(
    planStart: Long, currentEnd: Long?, stableEnd: Long?, months: Int?,
    currentFixed: Map<String, Double>, stableFixed: Map<String, Double>, income: Int
) = BudgetPlanDB(
        goal = true,
        goal_percent = goal_percent,
        current_plan_end = currentEnd,
        current_month_allocation = current_month,
        current_savings = current_savings,
        current_leftover = current_leftover,
        stable_plan_end = stableEnd,
        stable_months_allocation = stable_months,
        stable_savings = stable_savings,
        stable_leftover = stable_leftover,
        months = months,
        plan_start = planStart,
        current_fixed = currentFixed,
        stable_fixed = stableFixed,
        income = income
)

fun BudgetPlanResponseSimple.toDb(
    planStart: Long, currentEnd: Long?,
    currentFixed: Map<String, Double>, stableFixed: Map<String, Double>, income: Int) = BudgetPlanDB(
        goal = false,
        current_plan_end = currentEnd,
        current_month_allocation = current_month,
        current_leftover = current_leftover,
        stable_months_allocation = stable_months,
        stable_leftover = stable_leftover,
        plan_start = planStart,
        current_fixed = currentFixed,
        stable_fixed = stableFixed,
        income = income
)