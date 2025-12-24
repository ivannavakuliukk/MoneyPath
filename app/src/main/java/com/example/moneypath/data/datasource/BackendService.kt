package com.example.moneypath.data.datasource

import com.example.moneypath.data.models.BudgetPlanRequest
import com.example.moneypath.data.models.BudgetPlanResponse
import com.example.moneypath.data.models.BudgetPlanResponseSimple
import com.example.moneypath.data.models.MinMonthsRequest
import com.example.moneypath.data.models.MinMonthsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendService {
    @POST("min_months")
    suspend fun getMinMonths(@Body request: MinMonthsRequest): MinMonthsResponse

    @POST("optimizer_goal")
    suspend fun getPlanWithGoal(@Body request: BudgetPlanRequest): BudgetPlanResponse

    @POST("optimize_simple")
    suspend fun getPlanSimple(@Body request: BudgetPlanRequest): BudgetPlanResponseSimple
}