package com.example.moneypath.data.repository

import android.util.Log
import coil.network.HttpException
import com.example.moneypath.data.datasource.BackendService
import com.example.moneypath.data.models.BudgetPlanRequest
import com.example.moneypath.data.models.BudgetPlanResponse
import com.example.moneypath.data.models.BudgetPlanResponseSimple
import com.example.moneypath.data.models.FixedExpenses
import com.example.moneypath.data.models.MinMonthsRequest
import com.example.moneypath.data.models.MinMonthsResponse
import javax.inject.Inject


class BackendRepository @Inject constructor(private val service: BackendService){

    suspend fun getMinMonths(income: Int, fixed_expenses: Int, bounds: List<List<Int>>, goal: Int):MinMonthsResponse?{
        return try{
            val response = service.getMinMonths(MinMonthsRequest( income, fixed_expenses, bounds, goal))
            if(response.error == null){
                response
            }else {
                Log.d("Backend Repository", "${response.error}")
                null
            }
        }catch (e: HttpException){
            Log.d("Backend Repository", "HTTP error ${e.message}")
            null
        }catch(e: Exception){
            Log.d("Backend Repository", "Other error ${e.message}")
            null
        }
    }

    suspend fun getOptimizedPlanGoal(data: BudgetPlanRequest): BudgetPlanResponse? {
        return try{
            val response = service.getPlanWithGoal(data)
            if(response.error == null) {response}
            else {
                Log.d("Backend Repository", "${response.error}")
                null
            }
        }catch (e: HttpException){
            Log.d("Backend Repository", "HTTP error ${e.message}")
            null
        }catch(e: Exception){
            Log.d("Backend Repository", "Other error ${e.message}")
            null
        }
    }

    suspend fun getOptimizedPlanSimple(data: BudgetPlanRequest): BudgetPlanResponseSimple? {
        return try{
            val response = service.getPlanSimple(data)
            if(response.error == null) {response}
            else {
                Log.d("Backend Repository", "${response.error}")
                null
            }
        }catch (e: HttpException){
            Log.d("Backend Repository", "HTTP error ${e.message}")
            null
        }catch(e: Exception){
            Log.d("Backend Repository", "Other error ${e.message}")
            null
        }
    }
}
