package com.example.moneypath.data.models

import java.lang.Error

data class BudgetPlanRequest(
    val balance: Double,
    val income: Int,
    val goal: Int? = null,
    val months: Int? = null,
    val fixed_expenses: FixedExpenses,
    val categories: List<String>,
    val bounds: List<List<Int>>,
    val priorities: List<Int>,
    val fixed_categories: List<String>,
    val fixed_amount_current: List<Int>,
    val fixed_amount_stable: List<Int>
)

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

data class BudgetPlanResponseSimple(
    val current_leftover: Double,
    val current_month: Map<String, Double>,
    val stable_months: Map<String, Double>,
    val stable_leftover: Double,
    val error: String? = null
)

data class FixedExpenses(
    val stable: Int,
    val current: Int
)

data class BudgetPlanDB(
    val isGoal: Boolean,
    val months: Int? = null,
    val current_leftover: Double,
    val current_month_allocation: Map<String, Double>,
    val current_savings: Double? = null,
    val goal_percent: Double? = null,
    val stable_months_allocation: Map<String, Double>,
    val stable_leftover: Double,
    val stable_savings: Double? = null,
    val current_plan_end: Long?,
    val stable_plan_end: Long? = null
)

// Мапери для перетворень в модель db
fun BudgetPlanResponse.toDb(currentEnd: Long?, stableEnd: Long?, months: Int?) = BudgetPlanDB(
    isGoal = true,
    goal_percent = goal_percent,
    current_plan_end = currentEnd,
    current_month_allocation = current_month,
    current_savings = current_savings,
    current_leftover = current_leftover,
    stable_plan_end = stableEnd,
    stable_months_allocation = stable_months,
    stable_savings = stable_savings,
    stable_leftover = stable_leftover,
    months = months
)

fun BudgetPlanResponseSimple.toDb(currentEnd: Long?) = BudgetPlanDB(
    isGoal = false,
    current_plan_end = currentEnd,
    current_month_allocation = current_month,
    current_leftover = current_leftover,
    stable_months_allocation = stable_months,
    stable_leftover = stable_leftover
)