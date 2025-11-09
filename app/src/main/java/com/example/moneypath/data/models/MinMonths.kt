package com.example.moneypath.data.models

data class MinMonthsRequest(
    val income: Int,
    val fixed_expenses: Int,
    val bounds: List<List<Int>>,
    val goal: Int
)

data class MinMonthsResponse(
    val min_months: Int? = null,
    val error: String? = null
)