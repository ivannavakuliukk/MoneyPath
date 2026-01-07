package com.example.moneypath.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.moneypath.data.models.BudgetPlanDB

class PlanParamsProvider: PreviewParameterProvider<BudgetPlanDB> {
    override val values = sequenceOf(
        BudgetPlanDB(
            months = 4,
            stable_savings = 9000.0,
            income = 40000,
            stable_fixed = mapOf("rent" to 5000.0, "subscription" to 700.0),
            stable_months_allocation = mapOf("food" to 5476.0, "shopping" to 3042.0, "cafe" to 2608.0),
            goal_percent = 32.6
        )
    )
}