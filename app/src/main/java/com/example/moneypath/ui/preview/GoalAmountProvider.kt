package com.example.moneypath.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class GoalPreviewParams(
    val goalAmount: Int,
    val goalTransactionsAmount: Double
)

class GoalAmountProvider: PreviewParameterProvider<GoalPreviewParams> {
    override val values = sequenceOf(
        GoalPreviewParams(goalAmount = 50000, goalTransactionsAmount = 0.0),
        GoalPreviewParams(goalAmount = 100000, goalTransactionsAmount = 100001.0),
        GoalPreviewParams(goalAmount = 500, goalTransactionsAmount = 250.0)
    )
}

class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
